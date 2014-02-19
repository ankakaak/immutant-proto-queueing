(ns proto-queue.core
  (:use [immutant.messaging :as msg :exclude [receive]]
        [immutant.messaging.hornetq :as hq]))

(def last-sent (atom nil))

(defn test-send
  "Send dat message"
  [message & [{:keys [priority ttl properties] :or {priority :high ttl 10000 properties {}}}]]
  (println  "Sending message: ", message)
  (reset! last-sent (msg/publish "queue.proto" message :priority priority :ttl ttl :properties properties)))


(def consumer (atom nil))


(defn set-queue-properties []
  (hq/set-address-options "queue.proto" {:last-value-queue true}))

(defn handle-message [message]
  (println "Message received:")
  (clojure.pprint/pprint message)

  )

(defn init-listener []
  (reset! consumer (msg/listen "queue.proto" handle-message)))

(defn stop-listener []
  (when @consumer
    (msg/unlisten @consumer)))


(comment
  (test-send {:text "hello10"} {:ttl 100000 :priority :low})
  (test-send {:text "hello20"} {:ttl 100000 :priority :high})

  (test-send {:text "unique 1"} {:ttl 100000 :priority :high :properties {"_HQ_LVQ_NAME" "there-can-only-be-one" :_HQ_LVQ_NAME "there-can-only-be-one"}})
  (test-send {:text "unique 2"} {:ttl 100000 :priority :high :properties {"_HQ_LVQ_NAME" "there-can-only-be-one" :_HQ_LVQ_NAME "there-can-only-be-one"}})

  @consumer

  (-> @last-sent
      (.getStringProperty "_HQ_LVQ_NAME")
      )


  (def t1 (immutant.messaging.hornetq/destination-controller "queue.proto"))
(-> t1
    (immutant.util/get-bean-property "_HQ_LVQ_NAME"))
t1
  (name :hello)

  (def t2 (msg/receive "queue.proto"))

  (meta t2)

  (clojure.pprint/pprint t2)

  (set-queue-properties)

  (init-listener)

  (stop-listener)

  (msg/stop "queue.proto")
  (msg/start "queue.proto" :last-value-queue true :address-full-message-policy :block)
  )
