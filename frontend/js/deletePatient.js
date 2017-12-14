$(document).ready(function(){
    console.log("populating patients in select...");
    var patients;
    $.ajax({
        url: 'http://localhost:8080/rest/system/retrieveAll',
        method: 'GET',
        contentType: 'application/json', //type of data recieved from server.
        success: function(res){ 
           console.log(res);
           patients = res;
        },
        error: function(err){
            console.log(err);
        }
    });
    setTimeout(function(){
        var listitems = '';
        JSON.parse(patients).forEach(function(p){
            var string = p.firstName + " " + p.lastName;
            listitems += '<option style={width: 100px; min-width: 50px; max-width: 50px;} value=' + p.id + '>' + string + '</option>';
        });
        $("#deleteselect").append(listitems);
        var id;
        $('#deleteselect').change(function(){ 
            id = $(this).val();
        });
        $("#delete").click(function(){
            console.log("delete clicked");
            $.ajax({
                url: 'http://localhost:8080/rest/system/deletePatient/'+id,
                method: 'DELETE',
                contentType: "application/json; charset=utf-8",
                success: function(res){ 
                    alert("Patient Successfully Deleted")
                    // console.log(res.responseText);
                },
                error: function(err){
                    console.log(err.responseText);
                    alert(err.responseText);
                }
            })
        })
    },5000);
});