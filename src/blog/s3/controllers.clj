(ns blog.s3.controllers
  (:require [blog.s3.handlers :as h]
            [ring.util.response :as rr]))

(defn generate-upload-url-controller! [environment]
  (fn [request]
    (let [file-input (-> request :parameters :body)
          upload-url (h/generate-upload-url!
                      file-input
                      environment)]
      (if (:s3/url upload-url)
        (rr/response upload-url)
        (rr/bad-request {:error "Something went wrong"})))))

(defn respond-uploaded-data-controller! [_environment]
  (fn [request]
    (let [req-body (-> request :parameters :body)]
      (rr/response req-body))))
