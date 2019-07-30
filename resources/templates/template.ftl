<#macro mainLayout title="OVO Komo Mock Server">
<!doctype html>
<html lang="en">
    <head>
        <title>${title}</title>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!-- Custom fonts for this template-->
        <link href="static/vendor/fontawesome-free/css/all.css" rel="stylesheet" type="text/css">
        <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
                  rel="stylesheet">

        <!-- Custom styles for this template-->
        <link href="static/css/sb-admin-2.css" type="text/css" rel="stylesheet" media="screen">
    </head>

    <body>
    <div class="container">
        <#nested/>
    </div>
    </body>
</html>
</#macro>