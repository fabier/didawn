<!-- Fixed navbar -->
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <g:link uri="/" class="navbar-brand danger">
                <span>
                    <g:img dir="images" file="favicons/favicon-32x32.png" class="logo-top-frontpage"/>
                    Didawn
                </span>
            </g:link>
        </div>

        <div id="navbar" class="collapse navbar-collapse">
            %{--<ul class="nav navbar-nav nopadding">--}%
            %{--<li>--}%
            %{--<a href="#">--}%
            %{--<i class="glyphicon glyphicon-dashboard"></i>--}%
            %{--&nbsp;Link One--}%
            %{--</a>--}%
            %{--</li>--}%
            %{--<li>--}%
            %{--<a href="#">--}%
            %{--<i class="glyphicon glyphicon-map-marker"></i>--}%
            %{--&nbsp;Link Two--}%
            %{--</a>--}%
            %{--</li>--}%
            %{--<li>--}%
            %{--<a href="#">--}%
            %{--<i class="glyphicon glyphicon-th-large"></i>--}%
            %{--&nbsp;Link Three--}%
            %{--</a>--}%
            %{--</li>--}%
            %{--</ul>--}%

            <ul class="nav navbar-nav navbar-right">
                <g:if test="${session.me}">
                    <li class="nopadding">
                        <g:link controller="public" action="logout" class="nopadding margin-8">
                            <img src="${session.me.images.first().url}" class="sp-image"/>
                        </g:link>
                    </li>
                </g:if>
                <g:else>
                    <li>
                        <g:link uri="${session.spUri}">
                            Login to Spotify
                        </g:link>
                    </li>
                </g:else>
                <sec:ifNotLoggedIn>
                    <li>
                        <g:link controller="login">
                            <i class="glyphicon glyphicon-log-in"></i>
                            &nbsp;<g:message code="default.connection"/>
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="register">
                            <i class="glyphicon glyphicon-plus-sign"></i>
                            &nbsp;<g:message code="default.register"/>
                        </g:link>
                    </li>
                </sec:ifNotLoggedIn>
                <sec:ifLoggedIn>
                    <li>
                        <a href="#">
                            <i class="glyphicon glyphicon-user"></i>
                            &nbsp;<sec:username/>
                        </a>
                    </li>
                    <li>
                        <g:link controller="logout">
                            <i class="glyphicon glyphicon-log-out"></i>
                            &nbsp;Déconnexion
                        </g:link>
                    </li>
                </sec:ifLoggedIn>
            </ul>
        </div>
    </div>
</nav>