<#import "template.ftl" as layout />
<@layout.mainLayout title="Modify API" header="API">

<form action="/api/new" method="post">

    <div class="form-group">
        <label for="name">Name</label>
        <input type="text" class="form-control" id="name" name="name" placeholder="Enter Name" value="${(api.name)!}"/>
    </div>

    <input type="hidden" id="apiResponse1" name="apiResponse" value="200#@$#{'name': '44546'}"/>
    <input type="hidden" id="apiResponse2" name="apiResponse" value="200#@$#{'name': '44546'}"/>
    <input type="hidden" id="apiResponse3" name="apiResponse" value="200#@$#{'name': '44546'}"/>
    <input type="hidden" id="apiResponse4" name="apiResponse" value="200#@$#{'name': '44546'}"/>

    <input type="hidden" id="id" name="id" value="${(api.id)!}"/>
    <button type="submit" class="btn btn-primary">Submit</button>

</form>

</@layout.mainLayout>