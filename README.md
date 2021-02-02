

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]


<p align="center">
  <a href="https://github.com/constellonautes/PIE_PROJECT">
    <img src="/images/logo.png" alt="drawing" width="150"/>
  </a>
</p>
  <h1 align="center">Optimisation de constellation hétérogène </h1>




## Getting Started

### Requirements :computer:
* Java-SE11 (or above) installed on your computer.
* [node.js](http://nodejs.org/)

### Launch the application :telescope:
1) Clone the repository.
   
2) On eclipse, go in the menu : file>import and in the popup window, select
   "import existing maven project". Then choose the path of the "project" folder.

3) On the package explorer panel, right click on PIE and then choose MAVEN>update project
   to build the project.
   
4) Run the program UseCase in useCase package. You can change the use case  number in the file Parameters in 
the package utils. You can load your own JSON input file in the input folder.


### Launch  the HMI :earth_americas:
* Install [node.js](http://nodejs.org/) from the official website.
* On Windows, you may need to [add node to the ```PATH ```](http://nodejs.org/) environment variable. 
* From the `HMI` root directory, run
```
npm install
npm start
```

or 
```
node server.js
```

  

* Browse to `http://localhost:8080/`

## Contribution guide
### If you work on the repository for the first time:
Go on github:
1)  Fork the repository 
<img src="/images/fork.png" alt="drawing" width="200"/>

2) Clone the repository on your local machine : copy  the url in the forked repo and type the
following line on the terminal:  ``git clone repo_url repo_name``
<img src="/images/url.png" alt="drawing" width="300"/>

3) Add your own remote repository:
``git remote add origin own_repo_url``

4) Add the common remote repository 
``git remote add upstream common_repo_url``

5) On eclipse, go in the menu : file>import and in the popup window, select 
"import existing maven project". Then choose the path of the "project" folder.

6) On the package explorer panel, right click on PIE and then choose MAVEN>update project 
to build the project.

###  To save the changes on your own  repository:

1) Visualize what has changed
``git status`` to see which files have changed
``git diff name_of_file`` to see what has changed inside a given file

2) **Run the script clean.sh** to clean eventual undesired changes in the classpath file 
 and in the target folder.
 * On Windows : ` bash clean.sh `
 * On Mac Os and Linux : ` ./clean.sh `
 
If you are running the script for the first time on windows, you need to enable 
the developer mode here : settings>update and security>for developers> 
 
> :warning: never commit the following files and folders:
> * .classpath
> * .settings
> * .bin/
> * .doc/
> * .metadata/
>
> :warning: Make sure that  there is no README deletion in the target folder.
>
> :white_check_mark: The repo is clean if only the file that you modified are in red or green 
>when you run `git status`

3) Stage and commit your changes
``git add all`` or ``git add name_of_file``
``git commit`` or ``git commit -m my_comment`` to add a comment

4) Push your changes on your own remote repo
``git push origin master``


###  To save the changes on the common repository:
1)  Pull the upstream branch, if it is not already done
``git pull upstream master``

2)  Eventually solve conflicts,then commit and push your changes on your own remote repo

4) On GITHUB create a pull request from your own repo 
<img src="/images/pull_request.png" alt="drawing" width="500"/>

> :warning: make sure that there is  "able to merge" displayed in green before submitting your request.

## Versions


**latest release :** 0.2.0
**latest version :** 0.2.1
List of the tagged versions:(https://github.com/constellonautes/PIE_PROJECT/tags)


## Authors

* **Julie Bayard**  _alias_ [@bayardj](https://github.com/bayardj)
* **Amélie Falcou**  _alias_ [@AmelieFalcou](https://github.com/AmelieFalcou)
* **Thibault Hermaszweski**  _alias_ [@ThibaultHerma](https://github.com/ThibaultHerma)
* **Loic Macé**  _alias_ [@loicmace](https://github.com/loicmace)
* **Théo Nguyen** _alias_ [@TheoNguyen611](https://github.com/TheoNguyen611)
* **Louis Rivoire**  _alias_ [@louis-rivoire](https://github.com/louis-rivoire)



list of the  [contributors](https://github.com/constellonautes/PIE_PROJECT/contributors) 

## Project directed and ordered by: 

* **Jeremie Labroquere**
* **Serge Rainjonneau** 



## License

This project is under the ``GNU GENERAL PUBLIC LICENSE`` - see the  [LICENSE](LICENSE) file for more informations.

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/constellonautes/PIE_PROJECT.svg?style=for-the-badge
[contributors-url]: https://github.com/constellonautes/PIE_PROJECT/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/constellonautes/PIE_PROJECT.svg?style=for-the-badge
[forks-url]: hhttps://github.com/constellonautes/PIE_PROJECT/network/members
[stars-shield]: https://img.shields.io/github/stars/constellonautes/PIE_PROJECT.svg?style=for-the-badge
[stars-url]: https://github.com/constellonautes/PIE_PROJECT/stargazers
[issues-shield]: https://img.shields.io/github/issues/constellonautes/PIE_PROJECT.svg?style=for-the-badge
[issues-url]: https://github.com/constellonautes/PIE_PROJECT/issues
[license-shield]: https://img.shields.io/github/license/constellonautes/PIE_PROJECT.svg?style=for-the-badge
[license-url]: https://github.com/constellonautes/PIE_PROJECT/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
[product-screenshot]: images/screenshot.png
