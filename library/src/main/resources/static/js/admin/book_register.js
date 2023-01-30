window.onload = () => {
    ComponentEvent.getInstance().addClickEventRegisterButton();
    ComponentEvent.getInstance().addClickEventImgAddButton();
    ComponentEvent.getInstance().addChangeImgFile();
    
}

const fileObj = {
    files: new Array()
}

class imgFileService {
    static #instance = null;
    static getInstance() {
        if(this.#instance == null) {
            this.#instance = new imgFileService();
        }
        return this.#instance;
    }

    getImgPreview() {
        const bookImg = document.querySelector(".book-img");

        const reader = new FileReader();

        reader.onload = (e) => {
            bookImg.src = e.target.result;
        }

        reader.readAsDataURL(fileObj.files[0]); // 파일의 이미지 url이 e로 들어간다.
    }
}

class ComponentEvent {
    static #instance = null;
    static getInstance() {
        if(this.#instance == null) {
            this.#instance = new ComponentEvent();
        }
        return this.#instance;
    }

    addClickEventRegisterButton() {
        const registerButton = document.querySelector(".register-button");

        registerButton.onclick = () => {
            if(confirm("도서 이미지를 등록하시겠습니까?")) {
                const imgAddButton = document.querySelector(".img-add-button");
                const imgRegisterButton = document.querySelector(".img-register-button");

                imgAddButton.disabled = false;
                imgRegisterButton.disabled = false;
            }else {
                location.reload();
            }
        }
    }

    addClickEventImgAddButton() {
        const imgFile = document.querySelector(".img-file");
        const addButton = document.querySelector(".img-add-button");

        addButton.onclick = () => {
            imgFile.click();
        }
    }

    addChangeImgFile() {
        const imgFile = document.querySelector(".img-file");
        imgFile.onchange = () => {
            const formData = new FormData(document.querySelector(".img-form"));
            let changeFlag = false;

            fileObj.files.pop(); // 이미지 들어있던 거 비우는 용도

            formData.forEach(value => {
                console.log(value);

                if(value.size != 0) {
                    fileObj.files.push(value);
                    changeFlag = true;
                }
            });

            if(changeFlag) {
                imgFileService.getInstance().getImgPreview();
                imgFile.value = null;
            }
        }
    }
}
