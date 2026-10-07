# EV Charging Network (Cavalry Microservices) — DevOps Lab 3

This repository contains a microservice-based application for an **Electric Vehicle Charging Network**, consisting of `cavalry-backend` and `notification-service` microservices.

The application is fully deployed and managed using **Kubernetes**.

---

## 📁 Kubernetes Manifests

All Kubernetes manifests are stored in the `k8s/` directory.

| File                       | Description                                                                                                     |
| -------------------------- | --------------------------------------------------------------------------------------------------------------- |
| `00-namespace.yaml`        | Creates a dedicated Kubernetes namespace called `vadym-cavalry`.                                                |
| `01-keycloak-realm.yaml`   | Contains the Keycloak realm export configuration in a `ConfigMap`.                                              |
| `02-configmap-secret.yaml` | Defines non-sensitive application configuration using a `ConfigMap` and sensitive credentials using a `Secret`. |
| `03-infrastructure.yaml`   | Deploys infrastructure components: PostgreSQL with a `PersistentVolumeClaim` and RabbitMQ message broker.       |
| `04-keycloak.yaml`         | Deploys the Keycloak authentication and authorization service.                                                  |
| `05-apps.yaml`             | Deploys the custom microservices: `cavalry-backend` with 2 replicas and `notification-service`.                 |

---

## 🚀 Deployment

To deploy the entire application stack from scratch onto a clean Kubernetes cluster, run the following command from the repository root:

```bash
kubectl apply -f k8s/
```

This command applies all Kubernetes manifests located in the `k8s/` directory.

### Verify Deployment Status

After applying the manifests, check the status of all deployed resources and persistent volume claims:

```bash
kubectl get all,pvc -n vadym-cavalry
```

The command should display:

* Pods
* Services
* Deployments
* ReplicaSets
* PersistentVolumeClaims

---

## 🌐 Access the Application

The `cavalry-backend` service is exposed using a Kubernetes `NodePort`.

If the application is running in **Minikube**, use the following command:

```bash
minikube service cavalry-backend -n vadym-cavalry
```

Minikube will open the service in the default browser or provide the corresponding service URL.

---

## ⚙️ Configuration

The application configuration is divided into two Kubernetes resources:

* `ConfigMap` — for non-sensitive configuration.
* `Secret` — for sensitive credentials.

### 1. ConfigMap

The `cavalry-config` ConfigMap stores non-sensitive environment configuration.

| Parameter                | Value      | Description                                                            |
| ------------------------ | ---------- | ---------------------------------------------------------------------- |
| `SPRING_PROFILES_ACTIVE` | `prod`     | Active Spring application profile.                                     |
| `SPRING_RABBITMQ_HOST`   | `rabbitmq` | Hostname of the RabbitMQ message broker inside the Kubernetes cluster. |

Example:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: cavalry-config
data:
  SPRING_PROFILES_ACTIVE: prod
  SPRING_RABBITMQ_HOST: rabbitmq
```

---

### 2. Secret

The `cavalry-secret` Kubernetes Secret stores sensitive application credentials.

| Parameter                 | Value          | Description                      |
| ------------------------- | -------------- | -------------------------------- |
| `POSTGRES_DB`             | `ev_charge_db` | PostgreSQL database name.        |
| `POSTGRES_USER`           | `root`         | PostgreSQL database username.    |
| `POSTGRES_PASSWORD`       | `rootpassword` | PostgreSQL database password.    |
| `KEYCLOAK_ADMIN`          | `admin`        | Keycloak administrator username. |
| `KEYCLOAK_ADMIN_PASSWORD` | `admin`        | Keycloak administrator password. |

> **Note:** These values are intended for the development/laboratory environment. Production deployments should use securely managed credentials and should not store plaintext passwords in the repository.

---

## 🏗️ Application Architecture

The application consists of several Kubernetes workloads that work together:

```text
                         ┌─────────────────────┐
                         │       Client        │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │  cavalry-backend    │
                         │    (2 replicas)     │
                         └──────┬─────────┬────┘
                                │         │
                    ┌───────────┘         └────────────┐
                    ▼                                  ▼
          ┌─────────────────┐                ┌─────────────────┐
          │   PostgreSQL    │                │     RabbitMQ     │
          │   ev_charge_db  │                │  Message Broker  │
          └─────────────────┘                └────────┬────────┘
                                                      │
                                                      ▼
                                           ┌─────────────────────┐
                                           │ notification-service │
                                           └─────────────────────┘

                         ┌─────────────────────┐
                         │      Keycloak       │
                         │ Authentication /    │
                         │    Authorization    │
                         └─────────────────────┘
```

### Main Components

**cavalry-backend**

* Main backend microservice.
* Runs with **2 replicas** for scalability and availability.
* Communicates with PostgreSQL.
* Uses RabbitMQ for asynchronous messaging.
* Uses Keycloak for authentication and authorization.

**notification-service**

* Dedicated microservice for processing notifications.
* Communicates with other services through RabbitMQ.

**PostgreSQL**

* Persistent relational database.
* Uses a Kubernetes `PersistentVolumeClaim` to preserve data.

**RabbitMQ**

* Message broker used for asynchronous communication between microservices.

**Keycloak**

* Provides authentication and authorization.
* Manages users, roles, and access to protected application resources.

---

## ☸️ Kubernetes Resources

All resources are deployed into the dedicated namespace:

```text
vadym-cavalry
```

The deployment order is organized through separate manifest files:

```text
k8s/
├── 00-namespace.yaml
├── 01-keycloak-realm.yaml
├── 02-configmap-secret.yaml
├── 03-infrastructure.yaml
├── 04-keycloak.yaml
└── 05-apps.yaml
```

This structure separates the infrastructure, configuration, authentication, and application components, making the Kubernetes deployment easier to maintain and understand.

---

## 🧪 Useful Kubernetes Commands

### List all resources

```bash
kubectl get all -n vadym-cavalry
```

### Check pods

```bash
kubectl get pods -n vadym-cavalry
```

### Check services

```bash
kubectl get services -n vadym-cavalry
```

### Check persistent volumes

```bash
kubectl get pvc -n vadym-cavalry
```

### Check deployments

```bash
kubectl get deployments -n vadym-cavalry
```

### View pod logs

```bash
kubectl logs <pod-name> -n vadym-cavalry
```

### Describe a pod

```bash
kubectl describe pod <pod-name> -n vadym-cavalry
```

### Delete the entire deployment

If you need to remove all resources created by the manifests:

```bash
kubectl delete -f k8s/
```

---

## 📌 Quick Start

For a clean Kubernetes cluster, the basic workflow is:

### 1. Deploy the application

```bash
kubectl apply -f k8s/
```

### 2. Check the deployment

```bash
kubectl get all,pvc -n vadym-cavalry
```

### 3. Access the backend

```bash
minikube service cavalry-backend -n vadym-cavalry
```

---

## 🛠️ Technologies

The project uses the following technologies:

* **Java / Spring Boot**
* **Spring AMQP / RabbitMQ**
* **PostgreSQL**
* **Keycloak**
* **Docker**
* **Kubernetes**
* **Minikube**
* **YAML**

---

## 👨‍💻 DevOps Lab 3

This project demonstrates the deployment and management of a microservice-based application using **Kubernetes**, including:

* Kubernetes namespaces
* ConfigMaps
* Secrets
* Deployments
* Services
* PersistentVolumeClaims
* PostgreSQL
* RabbitMQ
* Keycloak
* Microservice deployment
* Multiple application replicas
* NodePort service exposure
* Kubernetes resource management
