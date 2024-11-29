# Introduction

Projet à but scolaire dans le cadre du cours d'algorithmes et architecture logicielles de cybersécurité

---

# Énoncé

Voir Énoncé [ici](Enoncé1.pdf)

---

# Configuration

- Cloner le repos
```bash
git clone https://github.com/Loris-Salvador/masi4-cyber-labo1.git
```

- Ajouter le fichier `passwords.properties` à la racine du projet et y ajouter les passwords suivants
```bash
KEYSTORE_PASSWORD=
KEYS_PASSWORDS=
```
Étant dans un projet de cyber sécurité ce fichier a juste pour but de montrer les bonnes pratiques à savoir de ne pas mettre les passwords directement sur le git. Cependant, nous utiliserons `P@ssw0rd` dans les deux cas car nous n'exposons absolument aucune données sensibles et nous voulons faciliter le développement par équipe

---

## Application regroupant les 4 principes cryptographiques

Nous utilisons RSA avec des certificats et une signature RSA/SHA-1.

- Authentification : Certificat du serveur, donc on sait à qui on envoie, plus vérification de la signature côté serveur pour être sûr que c'est le client.
- Non-répudiation : Signature du message avec la clé privée. Le client ne peut donc pas nier avoir envoyé le message, étant le seul en possession de cette clé.
- Confidentialité : Chiffrement avec RSA.
- Intégrité : La signature étant hachée, toute altération entraînera une erreur côté serveur lors de la vérification de la signature.

## Bonus : Laisser le déni plausible

Pour cela, nous devons signer avec une clé partagée. Cette clé sera générée grâce à l'algorithme de Diffie-Hellman et signée par la clé privée, afin de garantir l'authentification.

Nous utiliserons donc RSA avec Certificats, signature avec cle privée et SHA-1 ainsi que HMAC avec MD5, Diffie-Hellman et AES

- Authentification :

  - Le serveur utilise un certificat pour prouver son identité (le client sait à qui il envoie les données).
  - Le client signe avec sa clé privée, ce qui permet au serveur de vérifier que le message provient bien du client.

- Confidentialité :

    - Les messages sont chiffrés avec l'algorithme symétrique AES, utilisant la clé partagée générée via Diffie-Hellman.

- Intégrité : Une signature HMAC est utilisée pour garantir que les messages n'ont pas été altérés.

- Déni plausible
Le message est signé avec une clé partagée. Cependant, comme cette clé est connue à la fois du client et du serveur, il devient impossible de prouver de manière irréfutable que le message a été généré par le client. En effet, le serveur, connaissant également la clé partagée, aurait très bien pu fabriquer le message ainsi que sa signature.