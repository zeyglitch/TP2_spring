-- Données de base pour le projet Pharmacie
-- Dispensaire (Etablissements de santé qui passent commande de médicaments)
-- Le fichier est chargé au démarrage de l''application

-- Insertion des catégories de médicaments
INSERT INTO CATEGORIE (CODE, LIBELLE, DESCRIPTION) VALUES
(DEFAULT, 'Antalgiques et Antipyrétiques', 'Médicaments contre la douleur et la fièvre'), -- code : 1
(DEFAULT, 'Anti-inflammatoires', 'Médicaments réduisant l''inflammation'), -- code : 2
(DEFAULT, 'Antibiotiques', 'Médicaments pour traiter les infections bactériennes'),
(DEFAULT, 'Antihypertenseurs', 'Médicaments pour traiter l''hypertension artérielle'),
(DEFAULT, 'Antidiabétiques', 'Médicaments pour traiter le diabète'),
(DEFAULT, 'Antihistaminiques', 'Médicaments pour traiter les allergies'),
(DEFAULT, 'Vitamines et Compléments', 'Suppléments nutritionnels'),
(DEFAULT, 'Médicaments Cardiovasculaires', 'Médicaments pour le cœur et la circulation'),
(DEFAULT, 'Médicaments Gastro-intestinaux', 'Médicaments pour les troubles digestifs'),
(DEFAULT, 'Médicaments Respiratoires', 'Médicaments pour les troubles respiratoires');


-- Catégorie 1: Antalgiques et Antipyrétiques
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Morphine 10mg', 1, 'Boîte de 14 comprimés', 25.80, 80, 0, 15, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400'),
('Doliprane Effervescent 1g', 1, 'Boîte de 8 comprimés', 3.50, 280, 0, 30, false, 'https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=400'),
('Efferalgan Vitamine C', 1, 'Boîte de 16 comprimés', 4.20, 220, 0, 25, false, 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400');

-- Catégorie 2: Anti-inflammatoires
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Étodolac 400mg', 2, 'Boîte de 14 comprimés', 12.50, 110, 0, 15, false, 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400'),
('Flurbiprofène 100mg', 2, 'Boîte de 30 comprimés', 10.80, 130, 0, 16, false, 'https://images.unsplash.com/photo-1550572017-edd951aa8f72?w=400');

-- Catégorie 3: Antibiotiques (2 médicaments indisponbibles)
INSERT INTO MEDICAMENT (NOM, CATEGORIE_CODE, QUANTITE_PAR_UNITE, PRIX_UNITAIRE, UNITES_EN_STOCK, UNITES_COMMANDEES, NIVEAU_DE_REAPPRO, INDISPONIBLE, imageURL) VALUES
('Lévofloxacine 500mg', 3, 'Boîte de 7 comprimés', 15.80, 160, 0, 18, true, 'https://images.unsplash.com/photo-1628771065518-0d82f1938462?w=400'),
('Clindamycine 300mg', 3, 'Boîte de 16 gélules', 13.20, 140, 0, 16, true, 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400');

-- Ajout pour TP

-- 1. Ajout de deux Dispensaires
-- Note : Les colonnes ADRESSE, VILLE, REGION, etc. viennent de l'objet @Embedded AdressePostale
INSERT INTO DISPENSAIRE (CODE, NOM, ADRESSE, CODE_POSTAL, VILLE, PAYS, REGION, CONTACT, TELEPHONE) 
VALUES ('D01', 'Dispensaire du Sud', '10 rue de la Paix', '31000', 'Toulouse', 'France', 'Occitanie', 'Dr. House', '0561000000');

INSERT INTO DISPENSAIRE (CODE, NOM, ADRESSE, CODE_POSTAL, VILLE, PAYS, REGION, CONTACT, TELEPHONE) 
VALUES ('D02', 'Dispensaire de Paris', '5 avenue des Champs', '75000', 'Paris', 'France', 'Ile-de-France', 'Pr. Raoult', '0145000000');


-- 2. Ajout de deux Commandes
-- Commande 1 : Passée par le Dispensaire D01 (Toulouse) le 10 Janvier 2025
INSERT INTO COMMANDE (NUMERO, SAISIE_LE, ENVOYEE_LE, PORT, REMISE, DESTINATAIRE, DISPENSAIRE_CODE, ADRESSE, VILLE, CODE_POSTAL, PAYS, REGION)
VALUES (1, '2025-01-10', NULL, 15.50, 0.0, 'M. Martin', 'D01', '10 rue de la Paix', 'Toulouse', '31000', 'France', 'Occitanie');

-- Commande 2 : Passée par le Dispensaire D02 (Paris) le 25 Décembre 2024
INSERT INTO COMMANDE (NUMERO, SAISIE_LE, ENVOYEE_LE, PORT, REMISE, DESTINATAIRE, DISPENSAIRE_CODE, ADRESSE, VILLE, CODE_POSTAL, PAYS, REGION)
VALUES (2, '2024-12-25', '2024-12-26', 10.00, 5.0, 'Mme. Curie', 'D02', '5 avenue des Champs', 'Paris', '75000', 'France', 'Ile-de-France');


-- 3. Ajout de Lignes de Commande (Contenu des commandes)
-- Commande 1 (Toulouse) contient 10 boites du médicament n°1
INSERT INTO LIGNE (QUANTITE, COMMANDE_NUMERO, MEDICAMENT_REFERENCE) VALUES (10, 1, 1);
-- Commande 1 contient aussi 5 boites du médicament n°2
INSERT INTO LIGNE (QUANTITE, COMMANDE_NUMERO, MEDICAMENT_REFERENCE) VALUES (5, 1, 2);

-- Commande 2 (Paris) contient 20 boites du médicament n°1
INSERT INTO LIGNE (QUANTITE, COMMANDE_NUMERO, MEDICAMENT_REFERENCE) VALUES (20, 2, 1);