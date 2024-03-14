# Distributed Tracing with OpenTelemetry

Simple example of distributed tracing with OpenTelemetry. Tracing backend Grafana Cloud, apps running on OpenShift with OpenTelemetry Operator for instrumenting the apps and collecting the traces.

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
```

### Test sample app

```bash
export ROUTE=http://$(oc get route app-a -o jsonpath='{.spec.host}')
curl $ROUTE
```

### Grafana traces

Got to Grafana Dashboard and navigate:  
Explore -> grafanacloud-<youruser>-traces -> Query type "Search"

-> no traces there

Apply the instrumentation:

```bash
oc apply -f k8s/instrumentation.yml
```

Then uncomment the instrumentation annotations in the deployment files and apply again. Make some calls to app-a and check again Grafana.
