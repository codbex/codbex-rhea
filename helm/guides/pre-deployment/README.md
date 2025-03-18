# Pre-deployment guide

<!-- TOC -->
* [Pre-deployment guide](#pre-deployment-guide)
  * [Installation options](#installation-options)
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
<!-- TOC -->

## Installation options

### Configured TLS (HTTPS protocol)

- Prerequisites
    - Configured DNS in OTC for your domain - for example for `eu3.codbex.com`
    - Record for testing subdomain (for example `rhea-demo`) which directs to the needed ELB IP

#### CCE ingress with autocreated ELB


#### CCE ingress with existing ELB

#### Install with nginx ingress and user provided certificate

#### Install with nginx ingress and cert manager generated certificate
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

### Disabled TLS (HTTP protocol)

#### Cloud Container Engine (CCE) ingress and autocreate ELB (default installation)

#### Install with existing CCE ELB for ingress


#### Install with nginx ingress


#### Install with LoadBalancer service

### Other installation configuration options

#### Install with configured user

#### Install with disabled volumes

#### Install with configured volumes

#### Install with configured resources
