# FindMyVelib
Here is a groupe project made with a classmate on android studio

Projet effectué par BAUTHIER Malaury et BENOIT Marion

Fonctionnalitées fonctionnant :
  - Affichage de la position du téléphone sur maps
  - Récupération des données de l'API Vélib
  - Affichage des bornes sur maps
  - Affichage des bornes préférées d'une couleur différente
  - Ouverture d'une activité avec les détails d'une station après avoir cliqué sur la borne correspondante
  - Affichage du nom, de l'adresse, du nombre de vélo disponible (électrique et mécanique compris), du nombre de borne, de la capacité et de la possibilité d'ajouter 
    la borne à ses favoris
  - Sauvegarde des favoris dans un fichier sur le téléphone
  - Enlevement d'une borne de ce fichier
  - En hors-ligne, maps affiche les bornes favorites et le détail de ces bornes comprend le nom, l'adresse et la capacité de ces bornes.

Problèmes rencontrés :
  - La connexion à l'API maps
  - La récupération des données de Vélib
  - La suppression de station favorites


BUG NON RESOLU !!!! Le fait de tester si le fichier Favoris existe ne fonctionne pas et supprime le fichier, la seule solution trouvée a été de ne pas faire ce test.
Par conséquent, sur un nouveau téléphone l'application va crasher car elle ne reconnait pas celui-ci. Il faut donc ouvrir la class MapsActivity, dans onCreate() 
décommenter ce qui est indiqué, runner, recommenter et runner à nouveau.
