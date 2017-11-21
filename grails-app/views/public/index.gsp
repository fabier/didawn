<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Didawn</title>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-sm-6 col-sm-offset-3">
            <g:if test="${session.accessToken}">
                <h1>Logged in Spotify OK</h1>
            </g:if>
            <g:else>
                <h1><g:link uri="${uri}">LOGIN TO SPOTIFY</g:link></h1>
            </g:else>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-6 col-sm-offset-3">
            <g:form controller="search" action="search" class="form-horizontal">
                <div class="form-group">
                    <label for="value" class="control-label col-sm-2">Search</label>

                    <div class="col-sm-6">
                        <input id="value" class="form-control" name="value" type="text" placeholder="Your search"
                               value="${searchTerm}">
                    </div>

                    <div class="col-sm-4">
                        <button type="submit" class="btn btn-default">Submit</button>
                    </div>
                </div>
            </g:form>
        </div>
    </div>

    <g:each in="${playlists}">
        <div class="row">
            <div class="col-sm-12">
                <g:link controller="playlist" action="show" id="${it.id}" params="[userId: it.owner.id]">
                    ${it.name}
                </g:link>
            </div>
        </div>
    </g:each>
</div>
</body>
</html>
