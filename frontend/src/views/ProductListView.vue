<template>
  <b-container>
    <div class="content">
      <div class="content-section">
        <div class="container">
          <div class="item" v-for="(product, index) in products" :key="index">
            <img id="product_img" :src="require('@/assets/menu/' + product.img)" style="width : 250px; height : 300px"/>
            {{ product.name }}<br />{{ product.price }} 원 <br />
            <button class="none" @click="add(product)">
              <img id="img_button" src="@/assets/add.png" v-show="loginUser.id" />
            </button>
            <button class="none" @click="movePage('/product-detail?id=' + product.id)">
              <img id="img_button" src="@/assets/shopping_list.png" v-show="loginUser.id" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <div id="banner" v-show="loginUser.id">
      <div>주문내역</div>
      <div v-for="(order, index) in form.olist" :key="index">
        <p>
          {{ order.name }} - {{ order.quantity }}개
          <button class="none" @click="minus(order)">
            <img src="@/assets/minus.png" style="width: 20px" />
          </button>
        </p>
      </div>
      <button @click="order()" v-show="form.olist.length">
        <img src="@/assets/shopping_cart.png" style="width: 20px; padding: 0px" />
        주문하기
      </button>
    </div>
  </b-container>
</template>

<script>
export default {
  name: "product-list-view",
  data() {
    return {
      form: {
        user_id: "",
        order_time: "",
        olist: [],
      },
    };
  },
  created() {
    this.$store.dispatch("selectProducts");
    this.$store.dispatch("selectOrders");
    this.$store.dispatch("selectUserInfo", {
      data: this.loginUser,
      callback: function (data) {
        this.userOrder = data;
        console.log(this.userOrder);
      },
    });
    console.log(this.loginUser.id);
    this.form.user_id = this.loginUser.id;
    console.log(this.products);
  },
  computed: {
    products() {
      return this.$store.getters.getProducts;
    },
    loginUser() {
      return this.$store.getters.getLoginUser;
    },
    orders() {
      return this.$store.getters.getOrders;
    },
  },
  methods: {
    movePage(url) {
      this.$router.push(url);
    },
    add(product) {
      let orders = this.$store.getters.getOrders;

      console.log(orders);
      let isSame = false;

      for (let temp of this.form.olist) {
        if (product.name == temp.name) {
          isSame = true;
          break;
        }
      }

      if (isSame) {
        let new_olist = {
          olist: [],
        };
        for (let temp of this.form.olist) {
          if (product.name == temp.name) {
            new_olist.olist.push({
              name: product.name,
              orderId: orders + 1,
              productId: product.id,
              quantity: temp.quantity + 1,
            });
          } else {
            new_olist.olist.push(temp);
          }
        }
        this.form.olist = new_olist.olist;
      } else {
        this.form.olist.push({
          name: product.name,
          orderId: orders + 1,
          productId: product.id,
          quantity: 1,
        });
      }
      console.log(this.form);
    },
    minus(order) {
      let orders = this.$store.getters.getOrders;
      let new_olist = {
        olist: [],
      };

      for (let temp of this.form.olist) {
        if (order.name == temp.name) {
          if (temp.quantity - 1 != 0) {
            new_olist.olist.push({
              name: order.name,
              orderId: orders + 1,
              productId: order.productId,
              quantity: temp.quantity - 1,
            });
          }
        } else {
          new_olist.olist.push(temp);
        }
      }

      this.form.olist = new_olist.olist;

      console.log(this.form);
    },
    order() {
      console.log(this.form);
      this.$store.dispatch("insertOrder", this.form);
      alert("주문이 완료되었습니다");
      this.$store.dispatch("selectUserInfo", {
        data: this.loginUser,
        callback: function (data) {
          this.userOrder = data;
          console.log(this.userOrder);
        },
      });

      this.form.olist = [];
    },
  },
};
</script>

<style scoped>
/* 중앙 Content */
.content {
  margin: 10px auto;
  max-width: 1200px;
}

.container {
  display: flex;
  flex-wrap: wrap;
  /* display: inline-flex; */
}
.item {
  border: 3px solid #f0e3db;
  border-radius: 30px;
  overflow: hidden;

  margin: 10px;
  text-align: center;
  width: 250px;
}
.none {
  background-color: #00000000;
  border: 0px;
}

#product_img {
  padding-bottom: 20px;
  object-fit: cover;
  width: 100%;
  min-height: 79%;
  height: auto;
}

#img_button {
  margin: 10px;
  width: 30px;
}

#banner {
  background: white;
  padding: 10px;
  border: 3px solid #f0e3db;
  border-radius: 10px;

  position: fixed; /*고정*/
  width: 180px; /*가로길이*/
  height: auto; /*세로길이*/
  right: 2px; /*위치 지정 : 오른쪽에서 몇 px에 위치, 왼쪽 배너이면 left : 2px; */
  top: 80px; /* 위치 지정 : 상단에서 몇 px에 위치, 하단부터라면 bottom:48px; */
  text-align: center; /*중앙정렬*/
}
</style>
