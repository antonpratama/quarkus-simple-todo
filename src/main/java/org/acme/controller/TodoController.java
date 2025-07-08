package org.acme.controller;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.*;
import org.acme.entity.Todo;
import org.acme.entity.Users;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/todos")
@RolesAllowed({"admin"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Todo Controller", description = "Todo Management API")
public class TodoController {

  @Inject
  JsonWebToken jwt;

  @GET
  @Path("/export")
  @Produces("text/csv")
  @Operation(summary = "Export all data to csv", description = "Export all data to csv")
  public Response exportCsv(){
    Long userId     = Long.parseLong(jwt.getClaim("upn"));
    Users user      = Users.findById(userId);
    List<Todo> listAllTodo = Todo.list("users", user);
//    List<Todo> listAllTodo = Todo.listAll();

    String header = "id,title,completed,dueDate";
    String csvData = listAllTodo.stream()
            .map(t -> t.id + "," + t.title + "," + t.completed + "," + t.dueDate)
            .collect(Collectors.joining("\n"));

    String output = header + "\n" + csvData;

    return Response.ok(output)
            .header("Content-Disposition", "attachment; filename=\"export-todos.csv\"")
            .build();
  }

  @GET
  @Path("/search-by-title")
  @Operation(summary = "Search Todos by title keyword", description = "Return All todo items based on keyword input, if keyword empty return all record")
  public Response searchByTitle(@QueryParam("keyword") String keyword,
                                @QueryParam("page") @DefaultValue("0") int page,
                                @QueryParam("size") @DefaultValue("10") int size) {
    long       total;
    List<Todo> todos;
    if (keyword == null || keyword.isEmpty()){
      todos = Todo.findAll().page(Page.of(page,size)).list();
      total = Todo.count();
    }
    else {
      todos = Todo.find("LOWER(title) LIKE ?1", "%" + keyword.toLowerCase() + "%").page(page, size).list();
      total = Todo.count("LOWER(title) LIKE ?1", "%" + keyword.toLowerCase() + "%");
    }

    return Response.status(Response.Status.OK).entity(new PagedResponse<>(total, page, size, todos)).build();
  }

  @GET
  @Operation(summary = "List All Todos", description = "Return All todo items, no filter")
  public Response getAll(){
    List<TodoResponse> listResponse = new ArrayList<>();
    List<Todo>         listTodo     = Todo.listAll();
    for (Todo todo : listTodo) {
      TodoResponse response = new TodoResponse();
      response.id         = todo.id;
      response.title      = todo.title;
      response.completed  = todo.completed;
      response.dueDate    = todo.dueDate;

      UserResponse user = new UserResponse();
      user.id       = todo.users.id.toString();
      user.username = todo.users.username;

      response.user       = user;

      listResponse.add(response);
    }
    return Response.status(Response.Status.OK).entity(listResponse).build();
  }

  @GET
  @Path("/search-by-status")
  @Operation(summary = "Search todo by completed status", description = "Search by completed status and use paging")
  public Response getAllByStatus(@QueryParam("completed") Boolean completed,
                                 @QueryParam("page") @DefaultValue("0") int page,
                                 @QueryParam("size") @DefaultValue("10") int size,
                                 @QueryParam("sort") String sortParam){
    PanacheQuery<Todo> query;
    Sort sort = Sort.empty();

    if (sortParam != null && !sortParam.isBlank()) {
      String[] parts = sortParam.split(",");
      String field = parts[0];
      String direction = (parts.length > 1) ? parts[1].toLowerCase() : "asc";

      if (List.of("title", "dueDate", "completed").contains(field)){
        sort = direction.equals("desc") ? Sort.descending(field) : Sort.ascending(field);
      }
    }

    long       total;
    List<Todo> todos;

    if (completed != null) {
      total = Todo.count("completed = ?1", completed);
      todos = Todo.find("completed = ?1", sort, completed)
                  .page(Page.of(page,size))
                  .list();
    } else {
      total = Todo.count();
      todos = Todo.findAll(sort)
              .page(Page.of(page,size))
              .list();
    }


    return Response.status(Response.Status.OK).entity(new PagedResponse<>(total, page, size, todos)).build();
  }

  @GET
  @Path("/search-by-user")
  @Operation(summary = "Search todo by user", description = "Search by user id")
  public Response getAllByStatus(@QueryParam("page") @DefaultValue("0") int page,
                                 @QueryParam("size") @DefaultValue("10") int size,
                                 @QueryParam("sort") String sortParam){
    Sort sort = Sort.empty();

    if (sortParam != null && !sortParam.isBlank()) {
      String[] parts = sortParam.split(",");
      String field = parts[0];
      String direction = (parts.length > 1) ? parts[1].toLowerCase() : "asc";

      if (List.of("title", "dueDate", "completed").contains(field)){
        sort = direction.equals("desc") ? Sort.descending(field) : Sort.ascending(field);
      }
    }

    long       totalData;
    int        totalPage;
    PanacheQuery<Todo> todos;
    List<TodoResponse> listResponse;

    long userid     = Long.parseLong(jwt.getClaim("upn"));

    if (userid > 0) {
      listResponse = new ArrayList<>();
      Users user = Users.findById(userid);
      totalData = Todo.count("users = ?1", user);
      todos = Todo.find("users = ?1", sort, user)
              .page(Page.of(page,size));
      totalPage = todos.pageCount();

      for (Todo todo : todos.list()) {
        TodoResponse response = new TodoResponse();
        response.id         = todo.id;
        response.title      = todo.title;
        response.completed  = todo.completed;
        response.dueDate    = todo.dueDate;

        UserResponse userResponse = new UserResponse();
        userResponse.id           = todo.users.id.toString();
        userResponse.username     = todo.users.username;

        response.user             = userResponse;

        listResponse.add(response);
      }
    } else {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }


    return Response.status(Response.Status.OK).entity(new PagedResponse<>(totalData, page, size, totalPage, listResponse)).build();
  }

  @POST
  @Transactional
  @Operation(summary = "Create new todo", description = "A normal create operation")
  public Response create(@Valid TodoRequest request){
    Long userId     = Long.parseLong(jwt.getClaim("upn"));
    Users user      = Users.findById(userId);

    Todo todo       = new Todo();
    todo.title      = request.title;
    todo.completed  = request.completed;
    todo.dueDate    = request.dueDate;
    todo.users      = user;
    todo.persist();
    return Response.status(Response.Status.CREATED).entity(toTodoResponse(todo)).build();
  }

  @GET
  @Path("/{id}")
  @Operation(summary = "Get todo by id", description = "get todo by id")
  public Response getByUd(@PathParam("id") Long id) {
    Todo todo = Todo.findById(id);
    if (todo == null) {
      throw new NullPointerException();
    }

    return Response.status(Response.Status.OK).entity(toTodoResponse(todo)).build();
  }

  @PUT
  @Path("/{id}")
  @Operation(summary = "Update todo by id", description = "A normal update operation")
  @Transactional
  public Response update(@PathParam("id") Long id, @Valid TodoUpdateRequest updatedTodo) {
    Todo todo = Todo.findById(id);
    if (todo == null) {
      throw new NullPointerException();
    }

    todo.title      = updatedTodo.title;
    todo.completed  = updatedTodo.completed;

    return Response.status(Response.Status.OK).entity(toTodoResponse(todo)).build();
  }

  @DELETE
  @Path("/{id}")
  @Transactional
  @Operation(summary = "Delete todo by id", description = "A hard delete operation")
  public Response delete(@PathParam(("id")) Long id){
    Todo.deleteById(id);

    return Response.status(Response.Status.OK).build();
  }

  private TodoResponse toTodoResponse(Todo todo){
    TodoResponse response = new TodoResponse();
    response.id = todo.id;
    response.title = todo.title;
    response.dueDate = todo.dueDate;

    return response;
  }
}
