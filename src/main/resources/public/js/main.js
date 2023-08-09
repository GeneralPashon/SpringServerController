const server_icon = document.getElementById("server_icon");

selectedServerId = "";

function selectServer(server){
    selectedServerId = server;
    server_icon.src = "/res/server-icons/" + server + ".png"
    server_name.innerHTML = "Minecraft / " + getServerInfoById(server).name;
}

function serverListItemHTML(id, displayName, game){
    return  "<li onclick=\"selectServer('" + id + "')\">    " +
            "   <div class='left_navbar_list_item'>         " +
            "       <img src='res/" + game + "-icon.png'>   " +
            "       <a>" + displayName + "</a>              " +
            "   </div>                                      " +
            "</li>                                          ";
}

const minecraftServerList = document.getElementById("minecraft_server_list");
const terrariaServerList = document.getElementById("terraria_server_list");
// terrariaServerList.innerHTML += serverListItemHTML("seregas_survival", "Serega's Survival", "terraria");

serverList = [];

fetch("/server/list", { method: "GET" })
.then(response => response.json())
.then(data => {
    for(var  i = 0; i < data.length; i++){
        var server = data[i];
        if(i == 0)
            selectedServerId = server.id;

        serverList[i] = server;
        minecraftServerList.innerHTML += serverListItemHTML(server.id, server.name, "minecraft");
    }
});

for(var  i = 0; i < serverList.length; i++){
    var server = serverList[i];

}

function getServerInfoById(id){
    for(var  i = 0; i < serverList.length; i++)
        if(serverList[i].id == id)
            return serverList[i];

    return null;
}



const startServerButton = document.getElementById("start_server_button");
startServerButton.addEventListener("click", () => {
    fetch("/server/start/" + selectedServerId, { method: "POST" }).then(response => {
        if(response.ok){
            startServerButton.disabled = true;
            stopServerButton.disabled = false;
            killServerButton.disabled = false;
        }
    });
});

const stopServerButton = document.getElementById("stop_server_button");
stopServerButton.addEventListener("click", () => {
    fetch("/server/stop/" + selectedServerId, { method: "POST" }).then(response => {
        if(response.ok){
            startServerButton.disabled = false;
            stopServerButton.disabled = true;
            killServerButton.disabled = true;
        }
    });
});

const killServerButton = document.getElementById("kill_server_button");
killServerButton.addEventListener("click", () => {
    fetch("/server/kill/" + selectedServerId, { method: "POST" }).then(response => {
        if(response.ok){
            startServerButton.disabled = false;
            stopServerButton.disabled = true;
            killServerButton.disabled = true;
        }
    });
});