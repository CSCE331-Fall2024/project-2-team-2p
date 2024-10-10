# project-2-team-2p

## How to develop ManagerFrame

Sorry to whoever has to do this but the structure is as follows
- The top just builds the main frame and adds all of the core components to it
    - This is the graph, the order panel, and the 3 subsections on the right
- All of the subsections are functions that declare how everything should look, they can be found by looking at the comments
- Right now all of the data is hardcoded, the only thing that needs to be done is to pull it from the database
- For submitting, all of the data is kept on the client. When you push to backend, the entire list is commited
    - I know this is an awful design, but just drop the table and write to it again (for the demo)
- This should be enough to connect the frontend to the backend, the rest is just frontend logic