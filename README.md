# Distributed Tracing with OpenTelemetry

Simple example of distributed tracing with OpenTelemetry. Tracing backend Grafana Cloud, apps running on OpenShift with OpenTelemetry Operator for instrumenting the apps and collecting the traces.

We have the following apps:

![The apps](./readme/apps.png "The apps")

Each app responds with it's name and we do a downstream service call, so app-a calls app-b and app-b calls app-c, adding the response of the downstream call to the output. So calling the app-a endpoint should return:

> App A <- App B <- App C

## Setup Grafana Tempo

One option is using the free tier of Grafana cloud; free tier is enough for testing, if no sensitive data is transferred. Alternatively, [setup Grafana / Tempo stack on OpenShift](https://docs.openshift.com/container-platform/4.14/distr_tracing/distr_tracing_tempo/distr-tracing-tempo-installing.html).

Make endpoint data of Tempo available via environment variables:

```bash
export TEMPO_URL=<tempourl>
export TEMPO_USER=<userid>
export TEMPO_APIKEY=<apikey>
```

## Configure OpenShift

### Setup OpenTelemetry

In OpenShift, install the OpenTelemetry operator from Operator Hub and accept the defaults.

### Create new project

```bash
oc new-project demo
```

### Create collector instance

```bash
export TEMPO_TOKEN=`echo -n "$TEMPO_USER:$TEMPO_APIKEY" | base64`

cat <<EOF |oc apply -f -
apiVersion: opentelemetry.io/v1alpha1
kind: OpenTelemetryCollector
metadata:
  name: otel
spec:
  config: |
    receivers:
      otlp:
        protocols:
          grpc:
          http:
    processors:
      batch:
    exporters:
      logging:
        loglevel: info
      otlp:
        endpoint: ${TEMPO_URL}
        headers:
          authorization: Basic ${TEMPO_TOKEN}
    service:
      pipelines:
        traces:
          receivers: [otlp]
          processors: [batch]
          exporters: [logging,otlp]
  mode: deployment
  resources: {}
  targetAllocator: {}
EOF
```

Check logs with `oc logs deployment/otel-collector`

## Testing with apps

### Deploy sample apps

```bash
oc apply -f k8s/app-a.yml
oc apply -f k8s/app-b.yml
oc apply -f k8s/app-c.yml
```

### Test sample app

```bash
export ROUTE=http://$(oc get route app-a -o jsonpath='{.spec.host}')
curl $ROUTE
```

### Grafana traces

Got to Grafana Dashboard and navigate:  
Explore -> grafanacloud-\<username\>-traces -> Query type "Search"

No traces to find. Now we want to change that so we have metrics flowing to Grafana / Tempo:

![Sending metrics](./readme/tempo-grafana.png "Sending metrics to Grafana / Tempo")

Create the instrumentation resource:

```bash
oc apply -f k8s/instrumentation.yml
```

Still no traces. Now we add the annotations so the apps are instrumented. We instrument only app-a and app-b, for app-c we use the OpenTelemetry Quarkus library (we could instrument the Quarkus app as well, but we want to show that you can mix and match apps with and without OpenTelemetry).

Set the opentelemetry annotations in **./k8s/app-a.yml** and **./k8s/app-b.yml** (lines 24 and 25) to **"true"**. Then configure the sampler of the Quarkus app (app-c) to sending 100% of the traces: **./k8s/app-c.yml** line 35, new value **1.0**. Then apply the deployment files again:

```bash
oc apply -f k8s/app-a.yml
oc apply -f k8s/app-b.yml
oc apply -f k8s/app-c.yml
```

Make some calls to the app-a endpoint and check again Grafana. Traces should be there now.
