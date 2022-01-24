(ns blog.s3.contracts)

(def S3PresignedUrlData
  [:map
   [:file-size number?]
   [:file-key string?]
   [:file-type string?]])

(def S3UploadedData
  [:map
   [:file-url string?]
   [:username string?]
   [:title string?]])
