{{- define "normalizeBoolean" -}}
  {{- if eq (. | toString | lower) "true" -}}
    {{- print "true" -}}
  {{- else -}}
    {{- print "false" -}}
  {{- end -}}
{{- end -}}
