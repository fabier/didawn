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

    <g:if test="${playlists}">
        <g:each in="${playlists.collate(6)}" var="playlistLine">
            <div class="row">
                <g:each in="${playlistLine}">
                    <div class="col-sm-2">
                        <g:link controller="playlist" action="show" id="${it.id}" params="[userId: it.owner.id]">
                            <img src="${it.images.first().url}" class="width-100"/>

                            <p class="text-center">
                                ${it.name}
                            </p>
                        </g:link>
                    </div>
                </g:each>
            </div>
        </g:each>
    </g:if>
</div>
</body>
</html>
