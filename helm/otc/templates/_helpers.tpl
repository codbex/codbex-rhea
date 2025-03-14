{{- define "normalizeBoolean" -}}
  {{- if eq (. | toString | lower) "true" -}}
    true
  {{- else -}}
    false
  {{- end -}}
{{- end -}}
