(ns blog.s3.handlers
  (:require [amazonica.aws.s3 :as client]
            [blog.s3.contracts :as c]
            [blog.s3.schemas :as s]))

(def max-file-size 10000)
(def allowed-file-types ["image/png" "image/jpg" "image/jpeg"])

(defn generate-signed-url [environment file-key]
  (let [{:keys [creds bucket]} (:s3 environment)]
    {:s3/url (client/generate-presigned-url creds {:bucket-name bucket
                                                   :method "PUT"
                                                   :key file-key})}))

(defn generate-upload-url!
  {:malli/schema [:=> [:cat c/S3PresignedUrlData :any] s/S3SignedURL]}
  [s3-data environment]
  (let [{:keys [file-type file-size file-key]} s3-data]
    (if (and (< file-size max-file-size) (some #(= file-type %) allowed-file-types))
      (generate-signed-url environment file-key)
      {:s3/error "Invalid file"})))
