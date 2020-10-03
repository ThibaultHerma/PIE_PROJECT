# PIE_PROJECT
main repository of the project:

To work on the repository: 
go on github:
1)  fork the repository 
2) clone the repository on your local machine : copy paste the url in the forked repo and then: 
  git clone repo_url repo_name
3) add your own remote repository:
   git remote add origin own_repo_url
4) add the common remote repository 
  git remote add upstream common_repo_url


 To save your changes your own  repository:
1) stage and commit your changes 
2) push your changes on your own remote repo
git push origin master

 To save your changes the common repository:
 
1) /!\ /!\ /!\ make sure you have the most updated work to avoid conflicts: fetch the upstream branch 
git fetch upstream 

2) merge the master branch of the upstream with your master branch 
git merge upstream/master master

3)  commit and push your changes on your own remote repo

4) on GITHUB create a pull request from your own repo 




