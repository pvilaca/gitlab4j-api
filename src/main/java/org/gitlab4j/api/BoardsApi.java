package org.gitlab4j.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.gitlab4j.api.models.Board;
import org.gitlab4j.api.models.BoardGroup;
import org.gitlab4j.api.models.BoardList;

/**
 * This class implements the client side API for the GitLab Issue Boards API calls.
 * 
 * NOTE: If a user is not a member of a group and the group is private, 
 *       a GET request on that group will result to a 404 status code.
 * 
 * @see <a href="https://docs.gitlab.com/ce/api/boards.html">GitLab Issue Boards API Documentaion</a>
 */
public class BoardsApi extends AbstractApi {

    public BoardsApi(GitLabApi gitLabApi) {
        super(gitLabApi);
    }

    /**
     * Lists Issue Boards in the given project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @return a list of project's issue boards
     * @throws GitLabApiException if any exception occurs
     */
    public List<Board> getBoards(Object projectIdOrPath) throws GitLabApiException {
        return (getBoards(projectIdOrPath, getDefaultPerPage()).all());
    }

    /**
     * Get all issue boards for the specified project using the specified page and per page setting
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param page the page to get
     * @param perPage the number of items per page
     * @return a list of project's Boards in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<Board> getBoards(Object projectIdOrPath, int page, int perPage) throws GitLabApiException {
        Response response = get(javax.ws.rs.core.Response.Status.OK, getPageQueryParams(page, perPage),
                "projects", getProjectIdOrPath(projectIdOrPath), "boards");
        return (response.readEntity(new GenericType<List<Board>>() {}));
    }

    /**
     * Get a Pager of all issue boards for the specified project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param itemsPerPage the number of items per page
     * @return a Pager of project's issue boards
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<Board> getBoards(Object projectIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<Board>(this, Board.class, itemsPerPage, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "boards"));
    }

    /**
     * Get a Stream of all issue boards for the specified project.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @return a Stream of project's issue boards
     * @throws GitLabApiException if any exception occurs
     */
    public Stream<Board> getBoardsStream(Object projectIdOrPath) throws GitLabApiException {
        return (getBoards(projectIdOrPath, getDefaultPerPage()).stream());
    }

    /**
     * Get a single issue board.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @return a Board instance for the specified board ID
     * @throws GitLabApiException if any exception occurs
     */
    public Board getBoard(Object projectIdOrPath, Integer boardId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, 
                "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId);
        return (response.readEntity(Board.class));
    }

    /**
     * Get an issue board as an Optional instance.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id</code></pre> 
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @return the Board instance for the specified board ID as an Optional instance
     */
    public Optional<Board> getOptionalBoard(Object projectIdOrPath, Integer boardId) {
        try {
            return (Optional.ofNullable(getBoard(projectIdOrPath, boardId)));
        } catch (GitLabApiException glae) {
            return (GitLabApi.createOptionalFromException(glae));
        }
    }

    /**
     * Creates a new Issue Board.
     *
     * <p>NOTE: This is only available in GitLab EE</p>
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/boards</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param name the name for the new board
     * @return the created Board instance
     * @throws GitLabApiException if any exception occurs
     */
    public Board createBoard(Object projectIdOrPath, String name) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("name", name, true);
        Response response = post(Response.Status.CREATED, formData.asMap(), "projects", getProjectIdOrPath(projectIdOrPath), "boards");
        return (response.readEntity(Board.class));
    }

    /**
     * Updates an existing Issue Board.
     *
     * <p>NOTE: This is only available in GitLab EE</p>
     *
     * <pre><code>GitLab Endpoint: PUT /projects/:id/boards/:board_id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance, required
     * @param boardId the ID of the board, required
     * @param name the new name of the board, optional (can be null)
     * @param assigneeId the assignee the board should be scoped to, optional (can be null)
     * @param milestoneId the milestone the board should be scoped to, optional (can be null)
     * @param labels a comma-separated list of label names which the board should be scoped to, optional (can be null)
     * @param weight the weight range from 0 to 9, to which the board should be scoped to, optional (can be null)
     * @return the updated Board instance
     * @throws GitLabApiException if any exception occurs
     */
    public BoardList updateBoard(Object projectIdOrPath, Integer boardId, String name, 
            Integer assigneeId, Integer milestoneId, String labels, Integer weight) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("name", name)
                .withParam("assignee_id", assigneeId)
                .withParam("milestone_id", milestoneId)
                .withParam("labels", labels)
                .withParam("weight", weight);
        Response response = put(Response.Status.OK, formData.asMap(),
                "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId);
        return (response.readEntity(BoardList.class));
    }

    /**
     * Soft deletes an existing Issue Board.
     *
     * <p>NOTE: This is only available in GitLab EE</p>
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/boards/:board_id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteBoard(Object projectIdOrPath, Integer boardId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId);
    }

    /**
     * Get a list of the board’s lists. Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id/lists</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @return a list of the issue board's lists
     * @throws GitLabApiException if any exception occurs
     */
    public List<BoardList> getBoardLists(Object projectIdOrPath, Integer boardId) throws GitLabApiException {
        return (getBoardLists(projectIdOrPath, boardId, getDefaultPerPage()).all());
    }

    /**
     * Get a list of the board’s lists for the specified project to using the specified page and per page setting.
     * Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id/lists</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @param page the page to get
     * @param perPage the number of Boards per page
     * @return a list of the issue board's lists in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<BoardList> getBoardLists(Object projectIdOrPath, Integer boardId, int page, int perPage) throws GitLabApiException {
        Response response = get(javax.ws.rs.core.Response.Status.OK, getPageQueryParams(page, perPage),
                "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId, "lists");
        return (response.readEntity(new GenericType<List<BoardList>>() {}));
    }

    /**
     * Get a Pager of the board’s lists. Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id/lists</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @param itemsPerPage the number of Board instances that will be fetched per page
     * @return a Pager of the issue board's lists
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<BoardList> getBoardLists(Object projectIdOrPath, Integer boardId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<BoardList>(this, BoardList.class, itemsPerPage, null,
                "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId, "lists"));
    }

    /**
     * Get a Stream of the board’s lists. Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id/lists</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @return a Stream of the issue board's lists
     * @throws GitLabApiException if any exception occurs
     */
    public Stream<BoardList> getBoardsListsStream(Object projectIdOrPath, Integer boardId) throws GitLabApiException {
        return (getBoardLists(projectIdOrPath, boardId, getDefaultPerPage()).stream());
    }

    /**
     * Get a single issue board list.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id/lists/:list_id</code></pre>
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @param listId the ID of the board lists to get
     * @return a BoardList instance for the specified board ID and list ID
     * @throws GitLabApiException if any exception occurs
     */
    public BoardList getBoardList(Object projectIdOrPath, Integer boardId, Integer listId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, 
                "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId, "lists", listId);
        return (response.readEntity(BoardList.class));
    }

    /**
     * Get a single issue board list as an Optional instance.
     *
     * <pre><code>GitLab Endpoint: GET /projects/:id/boards/:board_id/lists/:list_id</code></pre> 
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @param listId the ID of the board lists to get
     * @return a BoardList instance for the specified board ID and list ID as an Optional instance
     */
    public Optional<BoardList> getOptionalBoardList(Object projectIdOrPath, Integer boardId, Integer listId) {
        try {
            return (Optional.ofNullable(getBoardList(projectIdOrPath, boardId, listId)));
        } catch (GitLabApiException glae) {
            return (GitLabApi.createOptionalFromException(glae));
        }
    }

    /**
     * Creates a new Issue Board list.
     *
     * <pre><code>GitLab Endpoint: POST /projects/:id/boards/:board_id/lists</code></pre> 
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @param labelId the ID of the label
     * @return the created BoardList instance
     * @throws GitLabApiException if any exception occurs
     */
    public BoardList createBoardList(Object projectIdOrPath, Integer boardId, Integer labelId) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("label_id", labelId, true);
        Response response = post(Response.Status.CREATED, formData, "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId, "lists");
        return (response.readEntity(BoardList.class));
    }

    /**
     * Updates an existing Issue Board list. This call is used to change list position.
     *
     * <pre><code>GitLab Endpoint: PUT /projects/:id/boards/:board_id/lists/:list_id</code></pre> 
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @param listId the ID of the list
     * @param position the new position for the list
     * @return the updated BoardList instance
     * @throws GitLabApiException if any exception occurs
     */
    public BoardList updateBoardList(Object projectIdOrPath, Integer boardId, Integer listId, Integer position) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("position", position, true);
        Response response = putWithFormData(Response.Status.OK, formData,
                "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId, "lists", listId);
        return (response.readEntity(BoardList.class));
    }

    /**
     * Soft deletes an existing Issue Board list. Only for admins and project owners.
     *
     * <pre><code>GitLab Endpoint: DELETE /projects/:id/boards/:board_id/lists/:list_id</code></pre> 
     *
     * @param projectIdOrPath the project in the form of an Integer(ID), String(path), or Project instance
     * @param boardId the ID of the board
     * @param listId the ID of the list
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteBoardList(Object projectIdOrPath, Integer boardId, Integer listId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "projects", getProjectIdOrPath(projectIdOrPath), "boards", boardId, "lists", listId);
    }
    
    
 
    /**
     * Lists Issue Boards in the given Group.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @return a list of group's issue boards
     * @throws GitLabApiException if any exception occurs
     */
    public List<BoardGroup> getGroupBoards(Object groupIdOrPath) throws GitLabApiException {
        return (getGroupBoards(groupIdOrPath, getDefaultPerPage()).all());
    }

    /**
     * Get all issue boards for the specified group using the specified page and per page setting
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards</code></pre>
     *
     * @param groupIdOrPath the project in the form of an Integer(ID), String(path), or Group instance
     * @param page the page to get
     * @param perPage the number of items per page
     * @return a list of group's boards in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<BoardGroup> getGroupBoards(Object groupIdOrPath, int page, int perPage) throws GitLabApiException {
        Response response = get(javax.ws.rs.core.Response.Status.OK, getPageQueryParams(page, perPage),
                "groups", getGroupIdOrPath(groupIdOrPath), "boards");
        return (response.readEntity(new GenericType<List<BoardGroup>>() {}));
    }

    /**
     * Get a Pager of all issue boards for the specified group.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param itemsPerPage the number of items per page
     * @return a Pager of group's issue boards
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<BoardGroup> getGroupBoards(Object groupIdOrPath, int itemsPerPage) throws GitLabApiException {
        return (new Pager<BoardGroup>(this, BoardGroup.class, itemsPerPage, null,
                "groups", getGroupIdOrPath(groupIdOrPath), "boards"));
    }

    /**
     * Get a Stream of all issue boards for the specified group.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @return a Stream of group's issue boards
     * @throws GitLabApiException if any exception occurs
     */
    public Stream<BoardGroup> getGroupBoardsStream(Object groupIdOrPath) throws GitLabApiException {
        return (getGroupBoards(groupIdOrPath, getDefaultPerPage()).stream());
    }

    /**
     * Get a single issue board.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id</code></pre>
     *
     * @param groupIdOrPath the project in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @return a BoardGroup instance for the specified board ID
     * @throws GitLabApiException if any exception occurs
     */
    public BoardGroup getGroupBoard(Object groupIdOrPath, Integer boardId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, 
                "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId);
        return (response.readEntity(BoardGroup.class));
    }

    /**
     * Get an issue board as an Optional instance.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id</code></pre> 
     *
     * @param groupIdOrPath the project in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @return the BoardGroup instance for the specified board ID as an Optional instance
     */
    public Optional<BoardGroup> getGroupOptionalBoard(Object groupIdOrPath, Integer boardId) {
        try {
            return (Optional.ofNullable(getGroupBoard(groupIdOrPath, boardId)));
        } catch (GitLabApiException glae) {
            return (GitLabApi.createOptionalFromException(glae));
        }
    }
    
   
    /**
     * Creates a new Issue Group Board.
     *
     * <p>NOTE: This is only available in GitLab EE</p>
     *
     * <pre><code>GitLab Endpoint: POST /groups/:id/boards</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param name the name for the new board
     * @return the created BoardGroup instance
     * @throws GitLabApiException if any exception occurs
     */
    public BoardGroup createGroupBoard(Object groupIdOrPath, String name) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("name", name, true);
        Response response = post(Response.Status.CREATED, formData.asMap(), "groups", getGroupIdOrPath(groupIdOrPath), "boards");
        return (response.readEntity(BoardGroup.class));
    }

    /**
     * Updates an existing Issue Board.
     *
     * <p>NOTE: This is only available in GitLab EE</p>
     *
     * <pre><code>GitLab Endpoint: PUT /groups/:id/boards/:board_id</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance, required
     * @param boardId the ID of the board, required
     * @param name the new name of the board, optional (can be null)
     * @param assigneeId the assignee the board should be scoped to, optional (can be null)
     * @param milestoneId the milestone the board should be scoped to, optional (can be null)
     * @param labels a comma-separated list of label names which the board should be scoped to, optional (can be null)
     * @param weight the weight range from 0 to 9, to which the board should be scoped to, optional (can be null)
     * @param hide_backlog_list hide the Open list, optional (can be null), default False
     * @param hide_closed_list Hide the Closed list, optional (can be null), default False
     * @return the updated Board instance
     * @throws GitLabApiException if any exception occurs
     */
    public BoardGroup updateGroupBoard(Object groupIdOrPath, Integer boardId, String name, 
            Integer assigneeId, Integer milestoneId, String labels, Integer weight,
            Boolean hide_backlog_list, Boolean hide_closed_list) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("name", name)
                .withParam("assignee_id", assigneeId)
                .withParam("milestone_id", milestoneId)
                .withParam("labels", labels)
                .withParam("weight", weight)
                .withParam("hide_backlog_list", hide_backlog_list)
                .withParam("hide_closed_list", hide_closed_list);
                
        Response response = put(Response.Status.OK, formData.asMap(),
                "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId);
        return (response.readEntity(BoardGroup.class));
    }

    /**
     * Soft deletes an existing Issue Board.
     *
     * <p>NOTE: This is only available in GitLab EE</p>
     *
     * <pre><code>GitLab Endpoint: DELETE /groups/:id/boards/:board_id</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteGroupBoard(Object groupIdOrPath, Integer boardId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId);
    }

    /**
     * Get a list of the board’s lists. Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id/lists</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @return a list of the issue group board's lists
     * @throws GitLabApiException if any exception occurs
     */
    public List<BoardList> getGroupBoardLists(Object groupIdOrPath, Integer boardId) throws GitLabApiException {
        return (getGroupBoardLists(groupIdOrPath, boardId, getDefaultPerPage()).all());
    }

    /**
     * Get a list of the board’s lists for the specified project to using the specified page and per page setting.
     * Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id/lists</code></pre>
     *
     * @param groupIdOrPath the project in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @param page the page to get
     * @param perPage the number of Boards per page
     * @return a list of the issue board's lists in the specified range
     * @throws GitLabApiException if any exception occurs
     */
    public List<BoardList> getGroupBoardLists(Object groupIdOrPath, Integer boardId, int page, int perPage) throws GitLabApiException {
        Response response = get(javax.ws.rs.core.Response.Status.OK, getPageQueryParams(page, perPage),
                "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId, "lists");
        return (response.readEntity(new GenericType<List<BoardList>>() {}));
    }

    /**
     * Get a Pager of the board’s lists. Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id/lists</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @param itemsPerPage the number of Board instances that will be fetched per page
     * @return a Pager of the issue board's lists
     * @throws GitLabApiException if any exception occurs
     */
    public Pager<BoardList> getGroupBoardLists(Object groupIdOrPath, Integer boardId, int itemsPerPage) throws GitLabApiException {
        return (new Pager<BoardList>(this, BoardList.class, itemsPerPage, null,
                "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId, "lists"));
    }

    /**
     * Get a Stream of the board’s lists. Does not include open and closed lists.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id/lists</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @return a Stream of the issue board's lists
     * @throws GitLabApiException if any exception occurs
     */
    public Stream<BoardList> getGroupBoardsListsStream(Object groupIdOrPath, Integer boardId) throws GitLabApiException {
        return (getGroupBoardLists(groupIdOrPath, boardId, getDefaultPerPage()).stream());
    }

    /**
     * Get a single issue board list.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id/lists/:list_id</code></pre>
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @param listId the ID of the board lists to get
     * @return a BoardList instance for the specified board ID and list ID
     * @throws GitLabApiException if any exception occurs
     */
    public BoardList getGroupBoardList(Object groupIdOrPath, Integer boardId, Integer listId) throws GitLabApiException {
        Response response = get(Response.Status.OK, null, 
                "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId, "lists", listId);
        return (response.readEntity(BoardList.class));
    }

    /**
     * Get a single issue board list as an Optional instance.
     *
     * <pre><code>GitLab Endpoint: GET /groups/:id/boards/:board_id/lists/:list_id</code></pre> 
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @param listId the ID of the board lists to get
     * @return a BoardList instance for the specified board ID and list ID as an Optional instance
     */
    public Optional<BoardList> getOptionalGroupBoardList(Object groupIdOrPath, Integer boardId, Integer listId) {
        try {
            return (Optional.ofNullable(getGroupBoardList(groupIdOrPath, boardId, listId)));
        } catch (GitLabApiException glae) {
            return (GitLabApi.createOptionalFromException(glae));
        }
    }

    /**
     * Creates a new Issue Group Board list.
     *
     * <pre><code>GitLab Endpoint: POST /groups/:id/boards/:board_id/lists</code></pre> 
     *
     * @param groupIdOrPath the project in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @param labelId the ID of the label
     * @return the created BoardList instance
     * @throws GitLabApiException if any exception occurs
     */
    public BoardList createGroupBoardList(Object groupIdOrPath, Integer boardId, Integer labelId) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm()
                .withParam("label_id", labelId, true);
        Response response = post(Response.Status.CREATED, formData, "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId, "lists");
        return (response.readEntity(BoardList.class));
    }

    /**
     * Updates an existing Issue Board list. This call is used to change list position.
     *
     * <pre><code>GitLab Endpoint: PUT /group/:id/boards/:board_id/lists/:list_id</code></pre> 
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @param listId the ID of the list
     * @param position the new position for the list
     * @return the updated BoardList instance
     * @throws GitLabApiException if any exception occurs
     */
    public BoardList updateGroupBoardList(Object groupIdOrPath, Integer boardId, Integer listId, Integer position) throws GitLabApiException {
        GitLabApiForm formData = new GitLabApiForm().withParam("position", position, true);
        Response response = putWithFormData(Response.Status.OK, formData,
                "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId, "lists", listId);
        return (response.readEntity(BoardList.class));
    }

    /**
     * Soft deletes an existing Issue Board list. Only for admins and project owners.
     *
     * <pre><code>GitLab Endpoint: DELETE /groups/:id/boards/:board_id/lists/:list_id</code></pre> 
     *
     * @param groupIdOrPath the group in the form of an Integer(ID), String(path), or Group instance
     * @param boardId the ID of the board
     * @param listId the ID of the list
     * @throws GitLabApiException if any exception occurs
     */
    public void deleteGroupBoardList(Object groupIdOrPath, Integer boardId, Integer listId) throws GitLabApiException {
        delete(Response.Status.NO_CONTENT, null, "groups", getGroupIdOrPath(groupIdOrPath), "boards", boardId, "lists", listId);
    }
    
}
