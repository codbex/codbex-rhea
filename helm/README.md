# Chart installation steps

<!-- TOC -->
* [Chart installation steps](#chart-installation-steps)
  * [Prerequisites](#prerequisites)
  * [Configured TLS (HTTPS protocol)](#configured-tls-https-protocol)
    * [CCE ingress with autocreated ELB](#cce-ingress-with-autocreated-elb)
    * [CCE ingress with existing ELB](#cce-ingress-with-existing-elb)
    * [Install with nginx ingress and user provided certificate](#install-with-nginx-ingress-and-user-provided-certificate)
    * [Install with nginx ingress and cert manager generated certificate](#install-with-nginx-ingress-and-cert-manager-generated-certificate)
  * [Disabled TLS (HTTP protocol)](#disabled-tls-http-protocol)
    * [Cloud Container Engine (CCE) ingress and autocreate ELB (default installation)](#cloud-container-engine-cce-ingress-and-autocreate-elb-default-installation)
    * [Install with existing CCE ELB for ingress](#install-with-existing-cce-elb-for-ingress)
    * [Install with nginx ingress](#install-with-nginx-ingress)
    * [Install with LoadBalancer service](#install-with-loadbalancer-service)
  * [Other installation configuration options](#other-installation-configuration-options)
    * [Install with configured user](#install-with-configured-user)
    * [Install with disabled volumes](#install-with-disabled-volumes)
    * [Install with configured volumes](#install-with-configured-volumes)
    * [Install with configured resources](#install-with-configured-resources)
  * [Other useful commands](#other-useful-commands)
    * [Template](#template)
    * [Cert manager logs](#cert-manager-logs)
    * [List all marketplace installations](#list-all-marketplace-installations)
    * [List actual values used for deployment in helm](#list-actual-values-used-for-deployment-in-helm)
    * [Uninstall testing chart](#uninstall-testing-chart)
    * [Uninstall marketplace chart](#uninstall-marketplace-chart)
    * [Test install](#test-install)
<!-- TOC -->

## Prerequisites
```shell
export KUBECONFIG='<path-to-your-kubeconfig>'

export GIT_REPO='<path-to-your-git-repo>'
export RELEASE_NAME='my-codbex-rhea'
export NAMESPACE='default'

export DOMAIN='eu3.codbex.com'
export SUBDOMAIN='rhea-demo'
```

## Configured TLS (HTTPS protocol)

- Prerequisites
  - Configured DNS in OTC for your domain - for example for `eu3.codbex.com`
  - Record for testing subdomain (for example `rhea-demo`) which directs to the needed ELB IP

### CCE ingress with autocreated ELB
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE
kubectl delete secret rhea-tls-secret

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-tls-otc-autocreate-elb.yaml

# update subdomain record set in OTC console to use the created ELB IP
# get the ELB IP using the command which is generated after the installation
curl -v https://$SUBDOMAIN.$DOMAIN/actuator/health/liveness
```

### CCE ingress with existing ELB
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-tls-cce-existing-elb.yaml

# update subdomain record set to use the existing ELB IP 
curl -v https://$SUBDOMAIN.$DOMAIN/actuator/health/liveness
```

### Install with nginx ingress and user provided certificate
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-tls-nginx-elb-user-cert.yaml

# update subdomain record set to use the nginx ELB IP 
curl -v https://$SUBDOMAIN.$DOMAIN/actuator/health/liveness
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
curl -v https://$SUBDOMAIN.$DOMAIN/actuator/health/liveness
```

## Disabled TLS (HTTP protocol)

### Cloud Container Engine (CCE) ingress and autocreate ELB (default installation)
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE

# get the generated IP using the command which is generated after the installation
export IP='80.158.44.137' 
curl -v http://$IP/actuator/health/liveness
```

### Install with existing CCE ELB for ingress
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-no-tls-cce-existing-elb.yaml

# set the IP of the existing CCE ELB
export IP='80.158.91.18'
curl -v http://$IP/actuator/health/liveness
```

### Install with nginx ingress
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-no-tls-nginx-ingress.yaml

# get the nginx ELB IP using the command which is generated after the installation
export IP='80.158.44.18'
curl -v http://$IP/actuator/health/liveness
```

### Install with LoadBalancer service
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE  --values ../example-values/values-no-tls-load-balancer-service.yaml

# get the IP using the command which is generated after the installation
export IP='80.158.91.18'
curl -v http://$IP/actuator/health/liveness

kubectl get service -n $NAMESPACE # service should be of type LoadBalancer
kubectl get ingress -n $NAMESPACE # ingress shouldn't be created
```

## Other installation configuration options

### Install with configured user
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-configured-user.yaml

# open the application and login with configured credentials
```

### Install with disabled volumes
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-disabled-volume.yaml
 
kubectl get pvc -n $NAMESPACE # should return nothing
```

### Install with configured volumes
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-configured-volume.yaml

kubectl get pvc -n $NAMESPACE # assert capacity `4Gi` and storage class `ssd`
```

### Install with configured resources
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-configured-resources.yaml

kubectl get deploy -o yaml -n $NAMESPACE # cpu 1 to 4, memory 1Gi to 8Gi
```

## Other useful commands

### Template
```shell
cd "$GIT_REPO/helm/otc"
helm template . --debug  --wait --namespace $NAMESPACE --values ../example-values/values-test.yaml
```

### Cert manager logs
```shell
# paste the following and click tab to autocomplete the pod
# example pod name `cert-manager-776d69949d-tmq2g `
kubectl logs -n cert-manager --follow cert-manager-
```

### List all marketplace installations
```shell
watch helm list --namespace $NAMESPACE
```

### List actual values used for deployment in helm
```shell
helm get values -n $NAMESPACE mkp-
```

### Uninstall testing chart
```shell
cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE
```

### Uninstall marketplace chart
```shell
helm uninstall --namespace $NAMESPACE mkp-
```

### Test install
```shell
helm uninstall --namespace $NAMESPACE mkp-

cd "$GIT_REPO/helm/otc"
helm uninstall $RELEASE_NAME --wait --namespace $NAMESPACE

# Render chart templates locally and display the output
helm template . --debug  --wait --namespace $NAMESPACE --values ../example-values/values-test.yaml

helm install $RELEASE_NAME . --wait --namespace $NAMESPACE --values ../example-values/values-test.yaml
```
