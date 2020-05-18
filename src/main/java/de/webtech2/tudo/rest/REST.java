package de.webtech2.tudo.rest;

import de.webtech2.tudo.dao.*;
import de.webtech2.tudo.dao.requestBuilder.ItemRequestBuilder;
import de.webtech2.tudo.model.Item;
import de.webtech2.tudo.model.Tag;
import de.webtech2.tudo.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/users")
@Transactional
public class REST {

    private final UserDAO userDAO = DAOCreator.create(UserDAO.class);
    private final ItemDAO itemDAO = DAOCreator.create(ItemDAO.class);
    private final TagDAO tagDAO = DAOCreator.create(TagDAO.class);

    

    @GET
    @Path("/")
    @RequiresRoles("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        if(SecurityUtils.getSubject().isAuthenticated())    // This should not be necessary. Apache Shiro bug?
        {
            List<User> users = new ArrayList<>();
            users = userDAO.getAll();
            for(int i=0;i<users.size();i++){
                users.set(i, users.get(i).redact());
            }
            return Response.status(Response.Status.OK).entity(users).build();
        }
        else
            return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postUsers(Map<String,Object> req) {
        String username;
        String email = null;
        String password;
        try {
            if (req.get("username") != null){
                username = (String) req.get("username");
            }else{
                throw new ClassCastException();
            }
            if(req.get("email")!=null){
                email = (String)req.get("email");
            }
            if(req.get("password")!=null) {
                password = (String) req.get("password");
            }else {
                throw new ClassCastException();
            }
            if(username.isEmpty())
                throw new ClassCastException();
            if(password.length()!=64||req.get("password").equals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"))
                return Response.status(Response.Status.BAD_REQUEST).build();
        }catch(ClassCastException cce){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if(userDAO.existsByUsername(username)){
            return Response.status(Response.Status.CONFLICT).build();
        }

        User user = userDAO.createUser(username, email, password);

        return Response.status(Response.Status.CREATED).entity(user.redact()).build();
    }

    @HEAD
    @Path("/{username}")
    public Response headUser(@PathParam("username") String username) {
        if (userDAO.existsByUsername(username)) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String username) {
        if (SecurityUtils.getSubject().isAuthenticated()&&userDAO.existsByUsername(username)) {
            return Response.status(Response.Status.OK).entity(userDAO.getByUsername(username).redact()).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response putUser(@PathParam("username") String username, Map<String, Object> req) {
        if(!userDAO.existsByUsername(username)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("user:put:" + username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        String email = null;
        String password = null;
        try {
            if(req.get("email")!=null)
                email = (String)req.get("email");
            if(req.get("password")!=null) {
                password = (String) req.get("password");
                if (password.length() != 64) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
            }
            if(username.isEmpty())
                throw new ClassCastException();

        }catch(ClassCastException cce){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        User user = userDAO.getByUsername(username);
        if(email!=null){
            user.setEmail(email);
        }
        if(password!=null){
            user.setPassword(password);
        }

        userDAO.update(user);

        return Response.status(Response.Status.NO_CONTENT).entity(user.redact()).build();

    }

    @DELETE
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("username") String username) {
        if (!userDAO.existsByUsername(username)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("user:delete:"+username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            userDAO.deleteUser(userDAO.getByUsername(username));
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }


    @GET
    @Path("{username}/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserItems(@PathParam("username") String username, @QueryParam("q") String search, @QueryParam("sortOrder") String sortOrderString, @QueryParam("sortCriteria") String sortCriteriaString, @QueryParam("tag") String tagString, @QueryParam("time") String timeString, @QueryParam("owner") String ownerString, @QueryParam("assignee") String assigneeString) {
        Set<Item> items = new HashSet<Item>();
        if(!userDAO.existsByUsername(username)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(!SecurityUtils.getSubject().isPermitted("item:get:"+username)){
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        ItemRequestBuilder itemRequestBuilder = ItemDAO.getRequestBuilder();
        itemRequestBuilder.setUser(userDAO.getByUsername(username));

        if(search!=null&&!search.isEmpty()){
            itemRequestBuilder.setTitle(search);
        }

        if(tagString!=null&&!tagString.isEmpty()){
            int tagId;
            try{
                tagId = Integer.parseInt(tagString);
            }catch(ClassCastException | NumberFormatException cce){
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (!SecurityUtils.getSubject().isPermitted("tag:get:" + username + ":" + tagId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            if (!tagDAO.exists(tagId)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            itemRequestBuilder.setTag(tagDAO.getById(tagId));
        }

        if(timeString!=null&&!timeString.isEmpty()) {
            Date compare = null;
            long currentTime = System.currentTimeMillis();
            switch (timeString.toLowerCase()) {
                case "day":
                    compare = new Date(currentTime - 1000L * 60L * 60L * 24L);
                    break;
                case "current":
                    compare = new Date(currentTime - 1000L * 60L * 60L * 24L * 3L);
                    break;
                case "week":
                    compare = new Date(currentTime - 1000L * 60L * 60L * 24L * 7L);
                    break;
                case "month":
                    compare = new Date(currentTime - 1000L * 60L * 60L * 24L * 30L);
                    break;
                case "year":
                    compare = new Date(currentTime - 1000L * 60L * 60L * 24L * 365L);
                    break;
                case "all":
                    compare = new Date(0L);
                    break;
                default:
                    return Response.status(Response.Status.BAD_REQUEST).build();
            }
            itemRequestBuilder.setCreatedSince(compare);
        }else{
            itemRequestBuilder.setCreatedSince(new Date(System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 3L));
        }

        if(sortOrderString==null||sortOrderString.isEmpty()){
            itemRequestBuilder.setSortOrder(ItemRequestBuilder.SortOrder.ASC);
        }else {
            try {
                ItemRequestBuilder.SortOrder sortOrder = null;
                sortOrder = ItemRequestBuilder.SortOrder.valueOf(sortOrderString.toUpperCase());
                itemRequestBuilder.setSortOrder(sortOrder);
            } catch (IllegalArgumentException iae) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        if(ownerString!=null){
            if(!ownerString.isEmpty()){
                if(!userDAO.existsByUsername(ownerString)){
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                itemRequestBuilder.setOwner(userDAO.getByUsername(ownerString));
            }
        }

        if(assigneeString!=null){
            if(!assigneeString.isEmpty()){
                if(!userDAO.existsByUsername(assigneeString)){
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                itemRequestBuilder.setAssignee(userDAO.getByUsername(assigneeString));
            }
        }

        if(sortCriteriaString==null|| sortCriteriaString.isEmpty()){
            itemRequestBuilder.setSortCriteria(ItemRequestBuilder.SortCriteria.CREATED);
        }else {
            try {
                ItemRequestBuilder.SortCriteria sortCriteria = null;
                sortCriteria = ItemRequestBuilder.SortCriteria.valueOf(sortCriteriaString.toUpperCase());
                itemRequestBuilder.setSortCriteria(sortCriteria);
            } catch (IllegalArgumentException iae) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        List<Item> ret = itemRequestBuilder.run();
        return Response.status(Response.Status.OK).entity(ret).build();
}
    @POST
    @Path("{username}/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postUserItems(@PathParam("username") String username, Map<String, Object> req) {
        if( !userDAO.existsByUsername(username)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("item:post:" + username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Item item = new Item();
        try {
            if (req.get("title") != null) {
                String title = (String) req.get("title");
                if (title.isEmpty()) {
                    throw new ClassCastException();
                }else{
                    item.setTitle(title);
                }
            }else{
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (req.get("description") != null)
                item.setDescription((String)req.get("description"));
            if (req.get("done") != null) {
                if ((boolean) req.get("done")) {
                    item.setDone(true);
                    item.setFinished(new Date());
                }else{
                    item.setDone(false);
                    item.setFinished(null);
                }
            }
            if (req.get("assignee")!=null){
                String assigneeString = (String)req.get("assignee");
                if(!userDAO.existsByUsername(assigneeString)){
                    return Response.status(Response.Status.NOT_FOUND).build();
                }else{
                    if(assigneeString.equals(username)){
                        item.setAssignee(null);
                    }else{
                        User assignee = userDAO.getByUsername(assigneeString);
                        item.setAssignee(assignee);
                    }
                }
            }
            if (req.get("tags") != null) {
                TagsItemsDAO.removeAllTags(item);
                Set<Integer> tags = new HashSet<Integer>((List<Integer>)req.get("tags"));
                for (Integer reqId :tags) {
                    if (tagDAO.exists(reqId)) {
                        TagsItemsDAO.addRel(tagDAO.getById(reqId), item);
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                }
            }
            item.setCreated(new Date());
        } catch (ClassCastException | NumberFormatException cce) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        item.setOwner(userDAO.getByUsername(username));
        itemDAO.add(item);

        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @PUT
    @Path("{username}/items/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putUserItem(@PathParam("user") String username, @PathParam("id") String idString, Map<String, Object> req) {
        int id;
        try{
            id = Integer.parseInt(idString);
        }catch(NumberFormatException nfe){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if( !userDAO.existsByUsername(username)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("item:put:" + username + ":" + id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!itemDAO.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Item item = itemDAO.getById(id);
        try {
            if (req.get("title") != null) {
                String title = (String) req.get("title");
                if (!title.isEmpty()) {
                    item.setTitle(title);
                }
            }
            if (req.get("description") != null)
                item.setDescription((String)req.get("description"));
            if (req.get("done") != null) {
                if ((boolean) req.get("done")) {
                    item.setDone(true);
                    item.setFinished(new Date());
                }else{
                    item.setDone(false);
                    item.setFinished(null);
                }
            }
            //if (req.get("due") != null)
            //    item.setDue(new Date((Integer) req.get("due")));
            if (req.get("assignee")!=null){
                String assigneeString = (String)req.get("assignee");
                if(!userDAO.existsByUsername(assigneeString)){
                    return Response.status(Response.Status.NOT_FOUND).build();
                }else{
                    if(assigneeString.equals(username)){
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }else{
                        User assignee = userDAO.getByUsername(assigneeString);
                        item.setAssignee(assignee);
                    }
                }
            }
            if (req.get("tags") != null) {
                TagsItemsDAO.removeAllTags(item);
                Set<Integer> tags = new HashSet<Integer>((List<Integer>)req.get("tags"));
                for (Integer reqId : tags) {
                    if (tagDAO.exists(reqId)) {
                        TagsItemsDAO.addRel(tagDAO.getById(reqId), item); } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                }
            }
        } catch (ClassCastException | NumberFormatException cce) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        itemDAO.update(item);

        return Response.status(Response.Status.OK).entity(itemDAO.getById(id)).build();
    }

    @GET
    @Path("{username}/items/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserItem(@PathParam("username") String username, @PathParam("id") String idString) {
        int id;
        try{
            id = Integer.parseInt(idString);
        }catch(ClassCastException | NumberFormatException cce){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("item:get:" + username + ":" + id) || !userDAO.existsByUsername(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!itemDAO.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(itemDAO.getById(id)).build();
    }


    @DELETE
    @Path("{username}/items/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserItem(@PathParam("username") String username, @PathParam("id") String idString) {
        int id;
        try{
            id = Integer.parseInt(idString);
        }catch(ClassCastException | NumberFormatException cce){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("item:delete:" + username + ":" + id) || !userDAO.existsByUsername(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!itemDAO.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        itemDAO.delete(itemDAO.getById(id));
        return Response.status(Response.Status.NO_CONTENT).build();
    }


    @GET
    @Path("{username}/tags")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTags(@PathParam("username") String username) {
        if(!userDAO.existsByUsername(username)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("tag:get:" + username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        User user = userDAO.getByUsername(username);
        List<Tag> tags = new ArrayList<>(tagDAO.getByUser(user));
        tags.sort(Tag::compareTo);
        return Response.status(Response.Status.OK).entity(tags).build();
    }

    @POST
    @Path("{username}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postUserTags(@PathParam("username") String username, Map<String, Object> req) {
        if(!userDAO.existsByUsername(username)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("tag:post:" + username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        Tag tag = new Tag();

        try {
            if (req.get("name") != null) {
                String name = (String) req.get("name");
                if (name.isEmpty()) {
                    throw new ClassCastException();
                } else {
                    tag.setName(name);
                }
            } else {
                throw new ClassCastException();
            }

            if (req.get("color")!=null){
                String color = (String)req.get("color");
                if (color.length()!=6){
                    throw new ClassCastException();
                }else {
                    tag.setColor(color);
                }
            } else {
                throw new ClassCastException();
            }

        } catch (ClassCastException | NumberFormatException cce) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        tag.setOwner(userDAO.getByUsername(username));

        tagDAO.add(tag);

        return Response.status(Response.Status.CREATED).entity(tag).build();
    }

    @PUT
    @Path("{username}/tags/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putUserTag(@PathParam("username") String username, @PathParam("id") String idString, Map<String, Object> req) {
        int id;
        try{
            id = Integer.parseInt(idString);
        }catch(NumberFormatException nfe){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if( !userDAO.existsByUsername(username)){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("tag:put:" + username + ":" + id)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!tagDAO.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Tag tag = tagDAO.getById(id);
        try {
            if (req.get("name") != null) {
                String name = (String) req.get("title");
                if (name.isEmpty()) {
                    throw new ClassCastException();
                } else {
                    tag.setName(name);
                }
            }
            if (req.get("color") != null) {
                String color = (String) req.get("color");
                if(color.length()!=6){
                    throw new ClassCastException();
                }else{
                    tag.setColor(color);
                }
            }
        } catch (ClassCastException | NumberFormatException cce) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        itemDAO.update(tag);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("{username}/tags/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTag(@PathParam("username") String username, @PathParam("id") String idString) {
        int id;
        try{
            id = Integer.parseInt(idString);
        }catch(ClassCastException | NumberFormatException cce){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("tag:get:" + username + ":" + id) || !userDAO.existsByUsername(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!tagDAO.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(tagDAO.getById(id)).build();
    }

    @DELETE
    @Path("{username}/tags/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserTag(@PathParam("username") String username, @PathParam("id") String idString) {
        int id;
        try{
            id = Integer.parseInt(idString);
        }catch(ClassCastException | NumberFormatException cce){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!SecurityUtils.getSubject().isPermitted("tag:get:" + username + ":" + id) || !userDAO.existsByUsername(username)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (!tagDAO.exists(id)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tagDAO.delete(tagDAO.getById(id));
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}