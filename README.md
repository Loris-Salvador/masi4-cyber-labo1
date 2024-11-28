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
Étant dans un projet de cyber sécurité ce fichier a juste pour but de montrer les bonnes pratiques à savoir de ne pas mettre les passwords directement sur le git. Cependant, nous utiliserons `P@ssw0rd` dans les deux cas

---

## Application regroupant les 4 principes cryptographiques

Nous utilisons RSA avec des certificats et une signature RSA/SHA-1.

- Authentification : Certificat du serveur, donc on sait à qui on envoie, plus vérification de la signature côté serveur pour être sûr que c'est le client.
- Non-répudiation : Signature du message avec la clé privée. Le client ne peut donc pas nier avoir envoyé le message, étant le seul en possession de cette clé.
- Confidentialité : Chiffrement avec RSA.
- Intégrité : La signature étant hachée, toute altération entraînera une erreur côté serveur lors de la vérification de la signature.