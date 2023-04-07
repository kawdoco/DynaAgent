import axios from "axios";

export const getWCScheduleTest = () => {
    const URL = `https://demo9906147.mockable.io/get/WCSchedule`;
    return axios.get(URL).then(res => res);
}

export const getWCSchedule = () => {
    const URL = `http://localhost:8080/api/shop-order/get`;
    return axios.get(URL).then(res => res);
}

export const getScheduledOrders = (skip, take) => {
    const URL = `http://localhost:8080/api/shop-order/get-scheduled-orders/${skip}/${take}`;
    return axios.get(URL).then(res => res);
}

export const addShopOrder = (shopOrder) => {
    const URL = `http://localhost:8080/api/shop-order/add-shop-order`;
    return axios.post(URL, shopOrder).then(res => res);
}

export const updateShopOrder = (shopOrder) => {
    const URL = `http://localhost:8080/api/shop-order/update-shop-order`;
    return axios.post(URL, shopOrder).then(res => res);
}

export const addShopOrderOperation = (operationDetails) => {
    const URL = `http://localhost:8080/api/shop-order/add-operation`;
    return axios.post(URL, operationDetails).then(res => res);
}

export const updateShopOrderOperation = (operationDetails) => {
    const URL = `http://localhost:8080/api/shop-order/update-operation`;
    return axios.post(URL, operationDetails).then(res => res);
}

export const changeOpStatusToUnschedule = (orderNo) => {
    const URL = `http://localhost:8080/api/shop-order/unschedule-op-status`;
    return axios.post(URL, orderNo).then(res => res);
}

export const getScheduledOrdersByWorkCenters = (skip, take) => {
    const URL = `http://localhost:8080/api/shop-order/get-scheduled-orders-by-wc/${skip}/${take}`;
    return axios.get(URL).then(res => res);
}

export const cancelShoporder = (orderNo) => {
    const URL = `http://localhost:8080/api/shop-order/cancel/${orderNo}`;
    return axios.get(URL).then(res => res);
}