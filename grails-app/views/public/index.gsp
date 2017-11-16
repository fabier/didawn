<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Didawn</title>
    <style type="text/css" media="screen">
    #status {
        background-color: #eee;
        border: .2em solid #fff;
        padding: 1em;
        float: left;
        -moz-box-shadow: 0px 0px 1.25em #ccc;
        -webkit-box-shadow: 0px 0px 1.25em #ccc;
        box-shadow: 0px 0px 1.25em #ccc;
        -moz-border-radius: 0.6em;
        -webkit-border-radius: 0.6em;
        border-radius: 0.6em;
        margin-bottom: 10px;
        margin-top: 5px;
    }

    #status ul {
        font-size: 0.9em;
        list-style-type: none;
        margin-bottom: 0.6em;
        padding: 0;
    }

    #status li {
        line-height: 1.3;
    }

    #status h1 {
        text-transform: uppercase;
        font-size: 1.1em;
        margin: 0 0 0.3em;
    }

    #page-body h1 {
        text-transform: uppercase;
        font-size: 1.1em;
    }

    h2 {
        margin-top: 1em;
        margin-bottom: 0.3em;
        font-size: 1em;
    }

    p {
        line-height: 1.5;
        margin: 0.25em 0;
    }

    #controller-list ul {
        list-style-position: inside;
    }

    #controller-list li {
        line-height: 1.3;
        list-style-position: inside;
        margin: 0.25em 0;
    }
    </style>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <g:if test="${session.accessToken}">
                <h1>Logged in Spotify OK</h1>
            </g:if>
            <g:else>
                <h1><g:link uri="${uri}">LOGIN TO SPOTIFY</g:link></h1>
            </g:else>
        </div>
    </div>

    <g:each in="${playlists}">
        <div class="row">
            <div class="col-md-12">
                <g:link controller="playlist" action="show" id="${it.id}" params="[userId: it.owner.id]">
                    ${it.name}
                </g:link>
            </div>
        </div>
    </g:each>
</div>
</body>
</html>
