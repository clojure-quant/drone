(ns drone.udp
  (:import [java.net DatagramSocket
            DatagramPacket
            InetSocketAddress]))

(defn send
  "Send a short textual message over a DatagramSocket to the specified
  host and port. If the string is over 512 bytes long, it will be
  truncated."
  [^DatagramSocket socket msg host port]
  (let [payload (.getBytes msg)
        length (min (alength payload) 512)
        address (InetSocketAddress. host port)
        packet (DatagramPacket. payload length address)]
    (.send socket packet)))

(defn receive
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
  (future (while true (f (receive socket)))))


(def socket (DatagramSocket. 8890))

(defn println2 [s]
  (println "RCVD: " s)
  ;; => Syntax error compiling at (src/drone/udp.clj:38:3).
  ;;    Unable to resolve symbol: s in this context

  )

;(receive-loop socket println2)


             
(send socket "command" "192.168.10.1" 8889)



(def socket-commands (DatagramSocket. 8889))

(receive-loop socket-commands println2)

(defn send-msg [s]
  (send socket-commands s "192.168.10.1" 8889))

(send-msg "command")

(send-msg "takeoff")
(send-msg "land")


(send-msg "up 10")
(send-msg "down 1")
(send-msg "up 1")

(send-msg "speed?")

(send-msg "battery?")
 

(send-msg "height?")

(send-msg "temp?")

(send-msg "attitude?")

(send-msg "wifi?")  ; get Wi-Fi SNR

(send-msg "tof?")

(send-msg "baro?")










