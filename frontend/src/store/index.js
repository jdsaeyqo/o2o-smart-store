import Vue from "vue";
import Vuex from "vuex";
import http from "@/util/http-common";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    isUsed: false,

    user: {
      id: "",
      name: "",
      pass: "",
      stamps: 0,
    },
    loginUser: {
      id: "",
      name: "",
      pass: "",
      stamps: 0,
    },
    products: [],
    orders: [],
    comments: [],
    ordersById: [],
    detailByOid: [],
    userInfo: [],
  },
  getters: {
    isUsed(state) {
      return state.isUsed;
    },
    getUser(state) {
      return state.user;
    },
    getLoginUser(state) {
      return state.loginUser;
    },
    getProducts(state) {
      return state.products;
    },
    getOrders(state) {
      return state.orders;
    },
    getComments(state) {
      return state.comments;
    },
    getOrdersById(state) {
      return state.ordersById;
    },
    getDetailByOid(state) {
      return state.detailByOid;
    },
    getUserInfo(state) {
      return state.userInfo;
    },
  },
  mutations: {
    IS_USED(state, data) {
      state.isUsed = data;
    },
    INSERT_USER(state, user) {
      state.user.id = user.id;
      state.user.name = user.name;
      state.user.pass = user.pass;
      state.user.stamps = user.stamps;
    },
    USER_INFO(state, data) {
      state.userInfo = data;
    },

    LOGIN_USER(state, loginUser) {
      state.loginUser.id = loginUser.id;
      state.loginUser.name = loginUser.name;
      state.loginUser.pass = loginUser.pass;
      state.loginUser.stamps = loginUser.stamps;
    },
    SELECT_PRODUCT(state, products) {
      state.products = products;
    },
    SELECT_ORDERS(state, orders) {
      state.orders = orders;
    },
    SELECT_COMMENT(state, comments) {
      state.comments = comments;
    },
    SELECT_ORDERS_BY_ID(state, orders) {
      state.ordersById = orders;
    },
    SELECT_DETAIL_BY_OID(state, orders) {
      state.detailByOid = orders;
    },
  },
  actions: {
    selectUserInfo({ commit }, payload) {
      let id = payload.data.id;
      console.log(id);
      http
        .get("rest/order/byUser", {
          params: { id: id },
        })
        .then(({ data }) => {
          console.log(data);
          commit("USER_INFO", data);
        });
    },

    isUsed({ commit }, payload) {
      let id = payload.data;
      http
        .get("rest/user/isUsed", {
          params: { id: id },
        })
        .then(({ data }) => {
          console.log(data);
          commit("IS_USED", data);
        });
    },

    insertUser({ commit }, payload) {
      console.log(payload);
      let user = payload.data;
      http
        .post("/rest/user", {
          id: user.id,
          name: user.name,
          pass: user.pass,
          stamps: user.stamps,
        })
        .then(({ data }) => {
          console.log(data);

          let msg = "등록 처리시 문제가 발생했습니다.";
          if (data === true) {
            msg = "등록이 완료되었습니다.";
            payload.callback();
            commit("INSERT_USER", data);
          }
          alert(msg);
        })
        .catch(() => {
          alert("등록 처리시 에러가 발생했습니다.");
        });
    },
    insertOrder(context, payload) {
      console.log(payload);
      http
        .post("rest/order", {
          userId: payload.user_id,
          orderTable: payload.order_table,
          details: payload.olist,
        })
        .then(() => {});
    },
    login({ commit }, payload) {
      let user = payload.data;
      http
        .post("/rest/user/login", {
          id: user.id,
          pass: user.pass,
        })
        .then(({ data }) => {
          console.log(data);
          commit("LOGIN_USER", data);
          payload.callback();
        })
        .catch(() => {
          alert("에러.");
        });
    },
    resetLoginUser({ commit }) {
      commit("LOGIN_USER", {
        id: "",
        name: "",
        pass: "",
        stamps: 0,
      });
    },
    selectProducts({ commit }) {
      http.get("rest/product").then(({ data }) => {
        commit("SELECT_PRODUCT", data);
      });
    },
    selectOrders({ commit }) {
      http.get("/rest/order/all").then(({ data }) => {
        commit("SELECT_ORDERS", data);
      });
    },
    selectComment({ commit }, payload) {
    

      http.get("/rest/product/" + payload).then(({ data }) => {
        console.log(data);

        commit("SELECT_COMMENT", data);
      });
    },
    selectByProduct({ commit }, payload) {
      http.get("rest/comment/" + payload).then(({ data }) => {
        commit("SELECT_COMMENT", data);
      });
    },
    insertComment(context, payload) {
      let data = payload.data;
      http
        .post("rest/comment", {
          id: data.id,
          userId: data.userId,
          productId: data.productId,
          rating: data.rating,
          comment: data.comment,
        })
        .then(() => {
          payload.callback();
        });
    },
    upComment(context, payload) {
      let data = payload.data;
      http
        .put("rest/comment", {
          id: data.id,
          userId: data.userId,
          productId: data.productId,
          rating: data.rating,
          comment: data.comment,
        })
        .then(() => {
          payload.callback();
        });
    },
    delComment(context, payload) {
      http.delete("rest/comment/" + payload.data).then(() => {
        payload.callback();
      });
    },
  },
  modules: {},
});
