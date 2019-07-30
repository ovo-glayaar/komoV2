<#import "template.ftl" as layout />
<@layout.mainLayout title="Users" header="Users">
<table class="table">
    <thead class="thead-dark">
        <tr>
            <th scope="col">Username</th>
            <th scope="col">Name</th>
            <th scope="col">Token</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
    <#list users as user>
    <tr>
        <td>${user.username}</td>
        <td>${user.name}</td>
        <td>${user.userToken.token}</td>
        <td>
            <a href="/user/edit?id=${user.id}" class="btn btn-secondary float-right mr-2" role="button">Edit</a>
            <a href="/user/delete?id=${user.id}" class="btn btn-danger float-right mr-2" role="button">Delete</a>
        </td>
    </tr>
    </#list>
    </tbody>
</table>
<div class="container">
    <div class="row">
        <a href="/user/new" class="btn btn-secondary float-right" role="button">New User</a>
    </div>
</div>
</@layout.mainLayout>