{{/*
   Converts a value to a real boolean (`true` or `false`).
   Supports both booleans (`true`/`false`) and string values (`"true"`/`"false"`).
   Usage: {{ include "isTrue" (dict "value" .Values.someFeature.enabled) | trim | eq "true" }}
*/}}
{{- define "isTrue" -}}
{{- if eq (typeOf .value) "bool" -}}
  {{- if .value }}true{{- else }}false{{- end }}
{{- else -}}
  {{- if eq (toString .value | lower) "true" }}true{{- else }}false{{- end }}
{{- end -}}
{{- end -}}
