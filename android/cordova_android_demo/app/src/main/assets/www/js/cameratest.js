function takePhoto(){
	var cameraOptions= {
    quality : 75,
    destinationType : Camera.DestinationType.DATA_URL,
    sourceType : Camera.PictureSourceType.CAMERA,     //照相机类型
    allowEdit : true,
    encodingType : Camera.EncodingType.JPEG,
    targetWdith : 100,
    targetHeight : 100,
    popoverOptions : CameraPopoverOptions,
    saveToPhotoAlbum : false
    };
    console.log("调用拍照接口");
	navigator.camera.getPicture(onCameraSuccess,onCameraError,cameraOptions);
}

function onCameraSuccess(imageURL){
	console.log("onCameraSuccess:"+imageURL);
	var image = document.getElementById('myImage');
	alert(imageURL);
    image.src = imageURL;
}

function onCameraError(e){
	console.log("onCameraError:"+e);
}

function takefromgalery(){
	var cameraOptions= {
    quality : 75,
    destinationType : Camera.DestinationType.FILE_URI,
    sourceType : Camera.PictureSourceType.SAVEDPHOTOALBUM,    //相册类型
    allowEdit : true,
    encodingType : Camera.EncodingType.JPEG,
    targetWdith : 100,
    targetHeight : 100,
    popoverOptions : CameraPopoverOptions,
    saveToPhotoAlbum : false
};
   console.log("调用相册接口");
    navigator.camera.getPicture(onCameraSuccess,onCameraError,cameraOptions);
}