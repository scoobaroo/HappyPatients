
console.log("populating update patient select");
$(document).ready(function(){
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
        $("#select").append(listitems);
        var id;
        $('#select').change(function(){ 
            id = $(this).val();
            var patient;
            JSON.parse(patients).forEach(function(p){
                if(p.id==id){
                    patient = p;
                }
            })
            var firstName = document.getElementById('firstName');
            firstName.value = patient.firstName;
            var lastName = document.getElementById('lastName');
            lastName.value = patient.lastName;
            var address = document.getElementById('address');
            address.value = patient.address;
            var birthDate = document.getElementById('birthDate');
            birthDate.value = patient.birthDate;
            var status = document.getElementById('status');
            status.value = patient.status;
            var phoneNumber = document.getElementById('phoneNumber');
            phoneNumber.value = patient.phoneNumber;
            var treatment = document.getElementById('treatment');
            treatment.value = patient.treatment;
            var diagnosis = document.getElementById('diagnosis');
            diagnosis.value = patient.diagnosis;
        });
        $("#update").click(function(){
            console.log("update clicked");
            var firstName = document.getElementById('firstName').value;
            var lastName = document.getElementById('lastName').value;
            var address = document.getElementById('address').value;
            var birthDate = document.getElementById('birthDate').value;
            var status = document.getElementById('status').value;
            var phoneNumber = document.getElementById('phoneNumber').value;
            var diagnosis = document.getElementById('diagnosis').value;
            var treatment = document.getElementById('treatment').value;
            jsonObject={
                "firstName":firstName,
                "lastName":lastName,
                "address":address,
                "phoneNumber":phoneNumber,
                "status":status,
                "birthDate":birthDate,
                "diagnosis":diagnosis,
                "treatment":treatment
            }
            $.ajax({
                url: 'http://localhost:8080/rest/system/updatePatient/'+id,
                method: 'PUT',
                data: JSON.stringify(jsonObject),
                contentType: "application/json; charset=utf-8",
                dataType: 'json',
                success: function(res){ 
                    alert("Patient Successfully Updated");
                    console.log(res.responseText);
                    // alert(JSON.parse(res).responseText);
                    window.location.href = "index.html";
                },
                error: function(err){
                    console.log(err.responseText);
                    alert(err.responseText);
                }
            })
        })
    },5000);

    $("#firstName").attr('required',true);
    $("#lastName").attr('required',true);
    $("#address").attr('required',true);
    $("#status").attr('required',true);
    $("#phoneNumber").attr('required',true);
    $("#birthDate").attr('required',true);
    $("#diagnosis").attr('required',true);
    $("#treatment").attr('required',true);
});