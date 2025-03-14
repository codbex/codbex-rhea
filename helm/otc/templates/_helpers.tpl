{{- define "normalizeBoolean" -}}
{{- if . | typeIs "string" -}}
  {{- if eq . "true" -}} true {{- else }} false {{- end -}}
{{- else if . | typeIs "bool" -}}
  {{- . }}
{{- else -}}
  false
{{- end -}}
{{- end -}}
