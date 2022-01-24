(ns blog.server
  (:require [ring.adapter.jetty :as jetty]
            [blog.router :as router])
  (:gen-class))

(defn server []
  (let [environment {:s3 {:bucket "bucket-name"
                          :creds {:access-key "aws-access-key"
                                  :endpoint "aws-region"
                                  :secret-key "aws-secret-key"}}}]
    (router/routes environment)))

(defn run-server []
  (jetty/run-jetty (server) {:port 4000 :join? false}))

(comment
  (run-server))
