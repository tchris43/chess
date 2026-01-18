# My notes
### Git:
git status just shows if there any untracked files or unstaged changes<br>
git log shows commit history<br>
git checkout . just restores your working directory to the last commit. So if no changes have been made then nothing will happen.<br>
git checkout (7 digits of id) notes.md will just revert the notes file and you can update and push that. But git checkout id will revert the whole directory<br>
### MARKDOWN: 
If you want to do a new line, just do >> instead of > and include '\<br>'
git commit -am will add and commit the file at the same time<br>


## Chess Functionality:
### Position indexing
The board goes from 1-8. This is why when you give a position, you give a number 
1-8. However, when you actually access the row an col from position,
you have to subtract 1 so that you can actually put it in the correct spot 
in the array which is indexed 0-7. If you do a for loop to access the array, you 
are not using position, and therefore can start your row and col at 0 so you don't have to subtract 1