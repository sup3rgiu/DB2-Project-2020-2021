(function() { // avoid variables ending up in the global scope

    function initImageUpload(box) {
        let uploadField = box.querySelector('.image-upload');

        uploadField.addEventListener('change', getFile);

        function getFile(e){
            let file = e.currentTarget.files[0];
            checkType(file);
        }

        function previewImage(file) {
            let thumb = box.querySelector('.js--image-preview'),
                reader = new FileReader();

            reader.onload = function() {
                thumb.style.backgroundImage = 'url(' + reader.result + ')';
            }
            reader.readAsDataURL(file);
            thumb.className += ' js--no-default';
        }

        function checkType(file) {
            const mimetypes = ['image/png', 'image/jpeg'];
            if (!mimetypes.includes(file.type)) {
                throw 'Invalid file type. Must be a .PNG or a .JPG';
            } else if (!file){
                throw 'No selected image';
            } else {
                document.getElementById('imagePreview').classList.remove("invalid");;
                previewImage(file);
            }
        }

    }

    // initialize box-scope
    var boxes = document.querySelectorAll('.box');

    for (let i = 0; i < boxes.length; i++) {
        let box = boxes[i];
        initImageUpload(box);
    }

})();

function imageInputInvalid() {
    document.getElementById('imagePreview').classList.add("invalid");;
}

function imageInputValid() {
    document.getElementById('imagePreview').classList.remove("invalid");
}