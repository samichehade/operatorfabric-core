# Helm

## Install


```sh
helm install --name operatorfabric operatorfabric/
```

```sh
helm install --name config config/
helm install --name registry registry/
helm install --name cards-consultation cards-consultation/
helm install --name cards-publication cards-publication/
helm install --name oauth oauth/
helm install --name thirds thirds/
helm install --name time time/
helm install --name users users/
helm install --name web-ui web-ui/
helm install --name client-gateway client-gateway/
```

## Remove

```sh
helm del --purge config
helm del --purge registry
helm del --purge cards-consultation
helm del --purge cards-publication
helm del --purge oauth
helm del --purge thirds
helm del --purge time
helm del --purge users
helm del --purge web-ui
helm del --purge client-gateway
```

## Upgrade

```sh
helm upgrade cards-publication cards-publication/
helm upgrade cards-consultation cards-consultation/
helm upgrade config config/
helm upgrade oauth oauth/
```

## Todo

- Env var name à templétiser
- liveness readness
- service static vs config (chart-config vs config en dur)