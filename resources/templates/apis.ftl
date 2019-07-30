<#import "template.ftl" as layout />
<@layout.mainLayout title="API List" header="API List">
<table class="table">
    <thead class="thead-dark">
        <tr>
            <th scope="col">Name</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
    <#list apis as api>
    <tr>
        <td>${api.name}</td>
        <td>
            <a href="/api/edit?id=${api.id}" class="btn btn-secondary float-right mr-2" role="button">Edit</a>
            <a href="/api/delete?id=${api.id}" class="btn btn-danger float-right mr-2" role="button">Delete</a>
        </td>
    </tr>
    </#list>
    </tbody>
</table>
<div class="container">
    <div class="row">
        <a href="/api/new" class="btn btn-secondary float-right" role="button">New API</a>
    </div>
</div>
</@layout.mainLayout>