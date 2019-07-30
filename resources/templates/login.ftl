<#import "template.ftl" as layout />
<@layout.mainLayout title="Login">

    <body class="bg-gradient-primary">

    <div class="row justify-content-center">

        <div class="col-xl-10 col-lg-12 col-md-9">

            <div class="card o-hidden border-0 shadow-lg my-5">
                <div class="card-body p-0">
                    <!-- Nested Row within Card Body -->
                    <div class="row">
                        <div class="col-lg-6 d-none d-lg-block bg-login-image"></div>
                        <div class="col-lg-6">
                            <div class="p-5">
                                <div class="text-center">
                                    <h1 class="h3 text-gray-900 mb-4">Welcome to Komo</h1>
                                    <h5 class="h5 text-gray-900 mb-4">Please login using your credential</h5>
                                </div>
                                <form class="user" action="/login" method="post">
                                    <div class="form-group">
                                        <input type="email" class="form-control form-control-user" id="inputEmail"
                                               aria-describedby="emailHelp" placeholder="Enter your OVO email address">
                                    </div>
                                    <div class="form-group">
                                        <input type="password" class="form-control form-control-user" id="inputPassword"
                                               placeholder="Enter your password">
                                    </div>
                                    <div class="form-group">
                                        <div class="custom-control custom-checkbox small">
                                            <input type="checkbox" class="custom-control-input" id="customCheck">
                                            <label class="custom-control-label" for="customCheck">Remember Me</label>
                                        </div>
                                    </div>
                                    <button type="submit" class="btn btn-primary btn-user btn-block">Login</button>
                                    <hr>
                                </form>
                                <hr>
                                <div class="text-center">
                                    <a class="small">Don't have an Account?</a>
                                    <a class="small" href="register.html">Click Here!</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>

    </body>

</@layout.mainLayout>