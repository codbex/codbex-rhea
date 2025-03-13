# Chart installation steps

<!-- TOC -->
* [Chart installation steps](#chart-installation-steps)
  * [Disabled TLS](#disabled-tls)
    * [Default installation (with OTC ingress and autocreate ELB)](#default-installation-with-otc-ingress-and-autocreate-elb)
    * [Install with existing OTC ELB for ingress](#install-with-existing-otc-elb-for-ingress)
    * [Install with nginx ingress](#install-with-nginx-ingress)
    * [Install with LoadBalancer service](#install-with-loadbalancer-service)
  * [Configured TSL](#configured-tsl)
    * [OTC with autocreated ELB](#otc-with-autocreated-elb)
    * [OTC with existing ELB](#otc-with-existing-elb)
    * [Install with nginx ingress and user provided certificate](#install-with-nginx-ingress-and-user-provided-certificate)
    * [Install with nginx ingress and cert manager generated certificate](#install-with-nginx-ingress-and-cert-manager-generated-certificate)
  * [Other installation configurations](#other-installation-configurations)
    * [Install with configured user](#install-with-configured-user)
    * [Install with disabled volumes](#install-with-disabled-volumes)
    * [Install with configured volumes](#install-with-configured-volumes)
    * [Install with configured resources](#install-with-configured-resources)
  * [Other useful commands](#other-useful-commands)
    * [Template](#template)
    * [Cert manager logs](#cert-manager-logs)
<!-- TOC -->

- Prerequisites
    ```shell
    export KUBECONFIG='<path-to-your-kubeconfig>'
  
    export GIT_REPO='<path-to-your-git-repo>'
    export RELEASE_NAME='my-codbex-rhea'
    export NAMESPACE='default'
    ```

## Disabled TLS

### Default installation (with OTC ingress and autocreate ELB)
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE

export IP='80.158.108.209'
curl http://$IP/actuator/health/liveness
```

### Install with existing OTC ELB for ingress
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-no-tls-existing-elb.yaml
 
export IP='80.158.91.18'
curl http://$IP/actuator/health/liveness
```

### Install with nginx ingress
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-no-tls-nginx-ingress-no-tls.yaml
  
export IP='80.158.44.18'
curl http://$IP/actuator/health/liveness
```

### Install with LoadBalancer service
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

export LB_IP='80.158.91.18'
helm install $RELEASE_NAME . --wait --namespace $NAMESPACE  --values ../example-values/values-no-tls-load-balancer-service.yaml

export IP='80.158.91.18'
curl http://$IP/actuator/health/liveness

kubectl get service -n $NAMESPACE # service should be of type LoadBalancer
kubectl get ingress -n $NAMESPACE # ingress shouldn't be created
```

## Configured TSL

- Prerequisites
  - Configured DNS in OTC for `eu3.codbex.com`
  - Record for subdomain `rhea-demo` which directs to the needed IP

### OTC with autocreated ELB
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-tls-otc-autocreate-elb.yaml

# update subdomain record set to use the created ELB IP 
curl https://rhea-demo.eu3.codbex.com/actuator/health/liveness
```

### OTC with existing ELB
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-tls-existing-elb.yaml

# update subdomain record set to use the existing ELB IP 
curl https://rhea-demo.eu3.codbex.com/actuator/health/liveness
```

### Install with nginx ingress and user provided certificate
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-tls-nginx-elb-user-cert.yaml

# update subdomain record set to use the nginx ELB IP 
curl https://rhea-demo.eu3.codbex.com/actuator/health/liveness
```

### Install with nginx ingress and cert manager generated certificate
- Prerequisites
  - Install cert manager in the cluster
    ```shell
      helm uninstall cert-manager -n cert-manager --wait
    
      helm repo add jetstack https://charts.jetstack.io
      helm repo update
    
      helm install cert-manager jetstack/cert-manager --set installCRDs=true --namespace cert-manager --create-namespace --version v1.9.1

    ```
  - Create file `cluster-issuer.yaml` with the following content
    ```yaml
    apiVersion: cert-manager.io/v1
    kind: ClusterIssuer
    metadata:
      name: letsencrypt
    spec:
      acme:
        # You must replace this email address with your own.
        # Let's Encrypt will use this to contact you about expiring
        # certificates, and issues related to your account.
        email: <YOUR_EMAIL>
        server: https://acme-v02.api.letsencrypt.org/directory
        privateKeySecretRef:
          # Secret resource that will be used to store the account's private key.
          name: letsencrypt
        # Add a single challenge solver, HTTP01 using nginx
        solvers:
          - http01:
              ingress:
                ingressTemplate:
                  metadata:
                    annotations:
                      "cert-manager.io/issue-temporary-certificate": "true"
                      "kubernetes.io/ingress.class": nginx 
                      "certmanager.k8s.io/cluster-issuer": letsencrypt
  
    ```
  - Replace `<YOUR_EMAIL>` with your email address
  - Apply the file `kubectl apply -f cluster-issuer.yaml -n cert-manager`

```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-tls-nginx-cert-manager.yaml

# update subdomain record set to use the nginx ELB IP 
curl https://rhea-demo.eu3.codbex.com/actuator/health/liveness
```


## Other installation configurations

### Install with configured user
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-configured-user.yaml

# test login with configured credentials
```

### Install with disabled volumes
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-disabled-volume.yaml
 
kubectl get pvc -n $NAMESPACE
```

### Install with configured volumes
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-configured-volume.yaml

kubectl get pvc -n $NAMESPACE
```

### Install with configured resources
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-configured-resources.yaml

kubectl get deploy -o yaml -n $NAMESPACE
```

## Other useful commands

### Template
```shell
helm template . --debug  --wait --namespace $NAMESPACE --values ../example-values/values-tls-nginx-elb.yaml

```

### Cert manager logs
```shell
# paste the following and click tab to autocomplete the pod
kubectl logs -n cert-manager --follow cert-manager-
```

### Uninstall marketplace chart
```shell
helm list --namespace $NAMESPACE
helm uninstall 

```
