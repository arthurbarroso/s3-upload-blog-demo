(ns blog.s3.routes
  (:require [blog.s3.contracts :as c]
            [blog.s3.controllers :as co]))

(defn routes [environment]
  ["/s3"
   ["/generate"
    {:post {:handler (co/generate-upload-url-controller! environment)
            :parameters {:body c/S3PresignedUrlData}}}]
   ["/store"
    {:post {:handler (co/respond-uploaded-data-controller! environment)
            :parameters {:body c/S3UploadedData}}}]])
