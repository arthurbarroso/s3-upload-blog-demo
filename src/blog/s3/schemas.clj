(ns blog.s3.schemas)

(def S3SignedURL
  [:or
   [:map
    [:s3/url string?]]
   [:map
    [:s3/error string?]]])
