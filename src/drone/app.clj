(ns drone.app
  (:require 
   [drone.udp :as drone :refer [send-command]])
  
  )


(drone/start-pinging)



@drone/status


(send-command :command)

(send-command :takeoff)
(send-command :land)

(send-command :emergency) ; Stop all motors immediately

(send-command [:up 20])
(send-command [:down 20])
(send-command [:forward 20])             ;20-500
(send-command [:back 20])            ;20-500

(send-command [:left 50]) ;20-500
(send-command [:right 50]) ;20-500

(send-command [:cw 1]) ; rotate x degree clockwise 1-3600
(send-command [:ccw 1]) ; rotate x degree counter-clockwise 1-3600

(send-command "go 1 1 1 1") ; fly to x y z in speed (cm/s) x: 20-500 y: 20-500 z: 20-500 speed: 10-100



(drone/fly-route
   [:takeoff]
   [:up 50]
   [:wait 5000]
   [:up 50]
   [:wait 5000]
   [:forward 50]
   [:wait 5000]
   [:back 50]
   [:wait 5000]
   [:left 50]
   [:wait 5000]
   [:right 50]
   [:wait 5000]
   [:land]) 


(send-command :speed?)
(send-command :battery?)
(send-command :height?)
(send-command :temp?)
(send-command :attitude?)
(send-command :wifi?)  ; get Wi-Fi SNR
(send-command :tof?)
(send-command :baro?)


; Receive Tello Video Stream
; Tello IP: 192.168.10.1 ->> PC/Mac/Mobile UDP Server: 0.0.0.0 UDP PORT:11111
;IP 0.0.0.0 via UDP PORT 11111.
;send “streamon” command to Tello via UDP SPORT 8889 to start the streaming

(send-command :streamon)
(send-command :streamoff)

