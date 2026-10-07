# 🧪 Kubernetes Deployment Demonstration

This section contains command-line logs demonstrating the deployment, self-healing, horizontal scaling, rolling update, and rollback capabilities of the Kubernetes cluster.

---

## 1. Applying Kubernetes Manifests

The Kubernetes manifests were applied to the cluster using:

```powershell
PS D:\uni\Java\cavalry> kubectl apply -f k8s/
namespace/vadym-cavalry created
configmap/keycloak-realm created
configmap/cavalry-config created
secret/cavalry-secret created
persistentvolumeclaim/postgres-pvc created
service/postgres created
deployment.apps/rabbitmq created
service/rabbitmq created
deployment.apps/keycloak created
service/keycloak created
deployment.apps/cavalry-backend created
service/cavalry-backend created
deployment.apps/notification-service created
```

All Kubernetes resources were successfully created.

---

## 2. Checking the Deployed Pods

After all components were started, the status of the deployed pods was checked:

```powershell
PS D:\uni\Java\cavalry> kubectl get pods -n vadym-cavalry
NAME                                    READY   STATUS    RESTARTS   AGE
cavalry-backend-579ddbd475-j9fwv        1/1     Running   0          4m56s
cavalry-backend-579ddbd475-tbtcm        1/1     Running   0          3m30s
keycloak-684656cfb6-s4b95               1/1     Running   0          16m
notification-service-5fb74fbfd-f79b2   1/1     Running   0          4m55s
postgres-7c448f7c96-jnmsf               1/1     Running   0          16m
rabbitmq-9b79cc8b5-cgkxc                1/1     Running   0          16m
```

All pods have the `Running` status and `1/1` readiness, confirming that all application components started successfully.

---

## 3. Demonstrating Self-Healing

To demonstrate Kubernetes self-healing, one of the `cavalry-backend` pods was manually deleted:

```powershell
PS D:\uni\Java\cavalry> kubectl delete pod cavalry-backend-579ddbd475-j9fwv -n vadym-cavalry
pod "cavalry-backend-579ddbd475-j9fwv" deleted
```

### Checking Pod Recreation

The pods were checked again to verify that Kubernetes automatically created a replacement pod:

```powershell
PS D:\uni\Java\cavalry> kubectl get pods -n vadym-cavalry
NAME                                    READY   STATUS    RESTARTS   AGE
cavalry-backend-579ddbd475-27bb2        1/1     Running   0          82s
cavalry-backend-579ddbd475-tbtcm        1/1     Running   0          14m
keycloak-684656cfb6-s4b95               1/1     Running   0          28m
notification-service-5fb74fbfd-f79b2   1/1     Running   0          3m55s
postgres-7c448f7c96-jnmsf               1/1     Running   0          28m
rabbitmq-9b79cc8b5-cgkxc                1/1     Running   0          28m
```

The deleted pod was automatically replaced with a new pod:

```text
cavalry-backend-579ddbd475-27bb2
```

This demonstrates Kubernetes' **self-healing mechanism**, provided by the `Deployment` and its underlying `ReplicaSet`.

---

## 4. Horizontal Scaling

The number of `cavalry-backend` replicas was increased from 2 to 3:

```powershell
PS D:\uni\Java\cavalry> kubectl scale deployment cavalry-backend --replicas=3 -n vadym-cavalry
deployment.apps/cavalry-backend scaled
```

### Checking Backend Replicas

The backend pods were then checked:

```powershell
PS D:\uni\Java\cavalry> kubectl get pods -n vadym-cavalry -l app=cavalry-backend
NAME                               READY   STATUS    RESTARTS   AGE
cavalry-backend-579ddbd475-27bb2   1/1     Running   0          2m15s
cavalry-backend-579ddbd475-4hnch   1/1     Running   0          82s
cavalry-backend-579ddbd475-tbtcm   1/1     Running   0          16m
```

The result shows that **3 `cavalry-backend` replicas** are running simultaneously.

---

## 5. Updating the Container Image

A **Rolling Update** was performed by updating the `cavalry-backend` image to version `1.1.0`:

```powershell
PS D:\uni\Java\cavalry> kubectl set image deployment/cavalry-backend backend=cavalry-backend:1.1.0 -n vadym-cavalry
deployment.apps/cavalry-backend image updated
```

### Monitoring the Rolling Update

The update process was monitored using:

```powershell
PS D:\uni\Java\cavalry> kubectl rollout status deployment/cavalry-backend -n vadym-cavalry
Waiting for deployment "cavalry-backend" rollout to finish: 1 out of 3 new replicas have been updated...
Waiting for deployment "cavalry-backend" rollout to finish: 2 out of 3 new replicas have been updated...
Waiting for deployment "cavalry-backend" rollout to finish: 1 old replicas are pending termination...
deployment "cavalry-backend" successfully rolled out
```

The rolling update was successfully completed. Kubernetes gradually replaced the old replicas with new ones.

---

## 6. Checking the Deployment Revision History

The deployment revision history was checked using:

```powershell
PS D:\uni\Java\cavalry> kubectl rollout history deployment/cavalry-backend -n vadym-cavalry
deployment.apps/cavalry-backend
REVISION  CHANGE-CAUSE
1         <none>
2         <none>
3         <none>
```

The deployment currently contains three recorded revisions.

---

## 7. Rolling Back to the Previous Revision

To demonstrate the **rollback** mechanism, the deployment was reverted to the previous revision:

```powershell
PS D:\uni\Java\cavalry> kubectl rollout undo deployment/cavalry-backend -n vadym-cavalry
deployment.apps/cavalry-backend rolled back
```

### Verifying the Rollback

The rollout status was checked to confirm that the rollback completed successfully:

```powershell
PS D:\uni\Java\cavalry> kubectl rollout status deployment/cavalry-backend -n vadym-cavalry
Waiting for deployment "cavalry-backend" rollout to finish: 2 out of 3 new replicas have been updated...
deployment "cavalry-backend" successfully rolled out
```

The rollback was successfully completed, and Kubernetes gradually replaced the current replicas with replicas from the previous revision.

---

## 📊 Demonstrated Kubernetes Features

The following Kubernetes capabilities were demonstrated during the laboratory work:

| Feature                | Demonstration                                                         |
| ---------------------- | --------------------------------------------------------------------- |
| **Deployment**         | Deployment of `cavalry-backend`, `notification-service`, and Keycloak |
| **Self-Healing**       | Automatic recreation of a deleted pod                                 |
| **Horizontal Scaling** | Increasing backend replicas from 2 to 3                               |
| **Rolling Update**     | Updating the backend image to version `1.1.0`                         |
| **Rollout Status**     | Monitoring the deployment update process                              |
| **Rollout History**    | Viewing deployment revision history                                   |
| **Rollback**           | Reverting the deployment to the previous revision                     |
| **Persistent Storage** | PostgreSQL using a `PersistentVolumeClaim`                            |
| **Service Discovery**  | Communication between components through Kubernetes Services          |
