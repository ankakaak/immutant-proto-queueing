(ns immutant.init
  (:use [immutant.messaging :as msg :exclude [receive]])
  (:require
   [immutant.daemons :as daemon]
   [immutant.registry :as registry]
   [immutant.util :as util]))


(println "starting queue")
(msg/start "queue.proto" :last-value-queue true :address-full-message-policy :block)
