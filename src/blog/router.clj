(ns blog.router
  (:require [muuntaja.core :as m]
            [reitit.coercion.malli :as coercion-malli]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.middleware.cors :refer [wrap-cors]]
            [blog.s3.routes :as s3]))

(defn router-config [_environment]
  {:data {:coercion coercion-malli/coercion
          :exception pretty/exception
          :muuntaja m/instance
          :middleware [muuntaja/format-middleware
                       exception/exception-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware
                       coercion/coerce-exceptions-middleware]}})

(defn api-router [environment]
  ["/v1"
   (s3/routes environment)])

(defn router [environment]
  (wrap-cors
   (ring/ring-handler
    (ring/router
     [""
      (api-router environment)]
     (router-config environment))
    (ring/create-default-handler))
   :access-control-allow-origin [#".*"]
   :access-control-allow-methods [:get :put :post :delete]
   :access-control-allow-credentials "true"))

(defn routes [environment]
  (router environment))
