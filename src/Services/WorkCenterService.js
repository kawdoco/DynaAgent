import axios from "axios";

export const interruptWorkCenter = (interruptData) => {
    const URL = `http://localhost:8080/api/work-center/interrupt`;
    return axios.post(URL, interruptData).then(res => res);
}

export const getWCDetails = () => {
    const URL = `http://localhost:8080/api/work-center/get`;
    return axios.get(URL).then(res => res);
}

export const addWorkCenter = (workCenterDetails) => {
    const URL = `http://localhost:8080/api/work-center/add-wc`;
    return axios.post(URL, workCenterDetails).then(res => res);
}

export const updateWorkCenter = (workCenterDetails) => {
    const URL = `http://localhost:8080/api/work-center/update-wc`;
    return axios.post(URL, workCenterDetails).then(res => res);
}