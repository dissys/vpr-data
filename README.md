# vpr-data

Release 2.0.18 - Added the token use for the SPARQL endpoints. Updated the dependencies.

Release 2.0.14 - Enhancement - Issue 2. ComponentDefinitions are hierarhically added as FunctionalComponents to link interactions
 
Release 2.0.13 - Bugfix. When interactions were reused in different modules, MapsTos on FunctionalComponents were not created for the reoccurances of interactions. This bug has been fixed.

Release 2.0.12 - Added additional SO terms to fetch interactions, fixed the issue related to referencing interactions

Release 2.0.11 - Interactions are added as references rather than copying each intearction

Release 2.0.10 - Added filtering by collection ids

Release 2.0.9  - Added caching. Interactions are added based on uniqueness of interaction types and participation roles

Release 2.0.6  - Initial release

## Adding the RDF Endpoint token as an environment variable

Some SPARQL endpoints, such as https://synbiohub.org/sparql, require a login token. Although vpr-data does not require this value to be stored, the unit tests require it to access SynBioHub. The token is read from the `SYNBIOHUB_TOKEN` environment variable. If the variable is not set, queries are sent without a token.

**1. Get a token** 
```
curl -X POST -H "Accept: text/plain" --data-urlencode "email=EMAIL" --data-urlencode "password=PWD" https://synbiohub.org/login
```

**2. Set the environment variable** (replace `TOKEN` with the token from step 1):

- macOS / Linux:
  ```
  echo "export SYNBIOHUB_TOKEN='TOKEN'" >> ~/.zshrc
  source ~/.zshrc
  ```
  Use `~/.bashrc` if your shell is bash. You can type `echo $SHELL` to verify.

Then type the following to print the first few characters for verification: 
```
echo ${SYNBIOHUB_TOKEN:0:8}
```
**3. Restart your terminal and IDE** so that they pick up the variable. 

To keep the VSCode is open, you can try this from the VSCode terminal. Not guaranteed to work, it seems to work for me!
```
launchctl setenv SYNBIOHUB_TOKEN "$SYNBIOHUB_TOKEN"
```

Alternatively, pass the token to Maven directly:
```
mvn test -Dsynbiohub.token=TOKEN
```