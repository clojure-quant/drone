(ns drone.udp
  (:import [java.net DatagramSocket
            DatagramPacket
            InetSocketAddress]))

(def command-port 8889)
(def status-port 8890)
(def drone-ip "192.168.10.1")


(defn send-msg
  "Send a short textual message over a DatagramSocket to the specified
  host and port. If the string is over 512 bytes long, it will be
  truncated."
  [^DatagramSocket socket msg host port]
  (let [payload (.getBytes msg)
        length (min (alength payload) 512)
        address (InetSocketAddress. host port)
        packet (DatagramPacket. payload length address)]
    (.send socket packet)))

(defn receive-msg
  "Block until a UDP message is received on the given DatagramSocket, and
  return the payload message as a string."
  [^DatagramSocket socket]
  (let [buffer (byte-array 512)
        packet (DatagramPacket. buffer 512)]
    (.receive socket packet)
    (String. (.getData packet)
             0 (.getLength packet))))

(defn receive-loop
  "Given a function and DatagramSocket, will (in another thread) wait
  for the socket to receive a message, and whenever it does, will call
  the provided function on the incoming message."
  [socket f]
  (future (while true (f (receive-msg socket)))))


(defonce socket-status (DatagramSocket. status-port))
(defonce socket-commands (DatagramSocket. command-port))

(def status (atom nil))

(defn process-status [s]
  (println "RCVD STATUS: " s)
  (reset! status s))

(receive-loop socket-status process-status)



;(receive-loop socket-commands #(println "RCVD command: " %))

(defn command->string 
  "helper function to use our DSL (domain specific language) 
   to describe drone commands"
  [[command & args]]
  (->> (conj args (name command))
       (map str) 
       (interpose " ")
       (apply str)))

(comment 
  (name :go)
   (command->string ["go" 1 2])  
   (command->string [:go 1 2])  
 ; 
  )

(defn send-command-string 
  "send a command to drone and wait for result
   drone-command is a string as required by trello api"
  [s]
  (send-msg socket-commands s drone-ip command-port)
  (receive-msg socket-commands))


(defn do-command [[c & args :as cmd]]
  (println "do-command: " c " : " args )
  (if (= c :wait)
      (let [time-ms (first args)]
        (println "waiting ms: " time-ms)
        (Thread/sleep time-ms)
        "wait finished!")
      (let [s (command->string cmd)]
        (println "send-drone-command: " s)
        (send-command-string s))))


(defn send-command 
  "send a single command to the drone
   valid commands are:
    [:wait 100]
    :emergency
    [:up 50]
   "
  [c & args]
  (let [c (if (vector? c)
              c
              (into [c] args))]
     (do-command c)))

(comment 
    (send-command [:wait 100])
    (send-command [:wait 1000])
    (send-command :wait 1000)
    (send-command [:up 50])
    (send-command :emergency)
 ; 
  )


(defn fly-route [& route-commands]
  (future
    (println "flying route with " (count route-commands) " commands...")
    (doall 
       (map send-command route-commands))
    (println "route finished!")))



(defn start-pinging []
  (future
    (loop []
       (Thread/sleep 10000) 
       ; If Tello does not receive any command input for 15 seconds,
       ; it will land automatically.
       (let [result (send-command :battery?)]
         (println "Drone Battery: " result))
       (recur))))











