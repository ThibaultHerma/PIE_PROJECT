

# Optimisation de constellation hétérogène




## Get Started
###  To work on the repository:
go on github:
1)  fork the repository 
<img src="/images/fork.png" alt="drawing" width="200"/>

2) clone the repository on your local machine : copy  the url in the forked repo and type the following line on the terminal:  ``git clone repo_url repo_name``
<img src="/images/url.png" alt="drawing" width="300"/>


3) add your own remote repository:
``git remote add origin own_repo_url``
4) add the common remote repository 
``git remote add upstream common_repo_url``

###  To save the changes on your own  repository:

1) visualize what has changed
``git status`` to see which files have changed
``git diff name_of_file`` to see what has changed inside a given file
2) stage and commit your changes
``git add all`` or ``git add name_of_file``
``git commit`` or ``git commit -m my_comment`` to add a comment
3) push your changes on your own remote repo
``git push origin master``

###  To save the changes on the common repository:
1) /!\ /!\ /!\ make sure you have the most updated work to avoid conflicts: fetch the upstream branch 
``git fetch upstream ``

2) merge the master branch of the upstream with your master branch 
``git merge upstream/master master``
( you can also do ``git pull upstream master``instead of fetch and merge )

3)  Eventually solve conflicts, then commit and push your changes on your own remote repo

4) on GITHUB create a pull request from your own repo 
<img src="/images/pull_request.png" alt="drawing" width="500"/>




## Versions


**Dernière version stable :** 1.0
**Dernière version :** 1.0
Liste des versions :(https://github.com/constellonautes/PIE_PROJECT/tags)


## Auteurs

* **Julie Bayard** 
* **Amélie Falcou** 
* **Thibault Hermaszweski**
* **Loic Macé** 
* **Théo Nguyen** _alias_ [@TheoNguyen611](https://github.com/TheoNguyen611)
* **Louis Rivoire** 



liste des [contributeurs](https://github.com/constellonautes/PIE_PROJECT/contributors) 

## Projet commandé et supervisé par: 

* **Jeremie Labroquere**
* **Serge Rainjonneau** 



## License

Ce projet est sous licence ``GNU GENERAL PUBLIC LICENSE`` - voir le fichier [LICENSE](LICENSE) pour plus d'informations




