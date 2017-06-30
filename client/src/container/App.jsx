import React from "react";
import {browserHistory, hashHistory, Link} from "react-router";
import {connect} from "react-redux";

import "./Body.scss";
import "./Header.scss";
import Dialog from "../components/Dialog.jsx";
import ajax from "../utils/ajax.js";

class App extends React.Component {
  constructor(props) {
    super(props);
    this.tagIndex = {
      "所有": 0,
      "国内": 1,
      "国际": 2,
      "社会": 3,
      "军事": 4,
      "航空": 5,
      "无人机": 6,
    };
    this.tagIndexToString = {
      "所有": "ALL",
      "国内": "GUONEI",
      "国际": "GUOJI",
      "社会": "SHEHUI",
      "军事": "JUNSHI",
      "航空": "HANGKONG",
      "无人机": "WURENJI",
    };
    var news = new Array(7);
    for (var i = 0; i < news.length; i++) {
      news[i] = {
        content: [],
        page: 0,
      }
    }
    this.tags = ["所有", "国内", "国际", "社会", "军事", "航空", "无人机"];
    this.state = {
      news: news,
      tag: "所有",
      login: false,
      nickname: "",
      tags: [],
      userlayer: false,
      taglayer: false,
    }
  }

  componentDidMount() {
    ajax.Get("/api/users", (r) => {
      this.setState({login: true, nickname: r.user.nickname, userlayer: false});
    }, (error) => {
      console.error(error);
    });

    this.loadNews(this.state.tag, false);
    document.addEventListener("scroll", () => {
      if (document.body.scrollTop + document.documentElement.clientHeight > document.body.scrollHeight - 800) {
        this.loadNews(this.state.tag, false);
      }
    })
  }

  login(callback) {
    var email = document.getElementById("email").value;
    var password = document.getElementById("password").value;
    if (email == "") {
      callback({status: -1, message: "请输入昵称"});
      return
    }
    if (password == "") {
      callback({status: -1, message: "请输入密码"});
      return
    }
    var data = "data:{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
    ajax.Post("/api/sessions", data, (r) => {
      this.setState({login: true, nickname: r.nickname, tags: "所有", userlayer: false});
    }, (error) => {
      console.error(error);
    });
  }

  register(callback) {
    var email = document.getElementById("email").value;
    var nickname = document.getElementById("nickname").value;
    var password = document.getElementById("password").value;
    var rePassword = document.getElementById("rePassword").value;

    var regex = /^([0-9A-Za-z\-_\.]+)@([0-9a-z]+\.[a-z]{2,3}(\.[a-z]{2})?)$/g;
    if (!regex.test(email)) {
      callback({status: -1, message: "邮箱的格式不合法"});
      return
    }
    if (nickname.length < 6) {
      callback({status: -1, message: "昵称长度小于6个字符"});
      return
    }
    if (password.length < 6) {
      callback({status: -1, message: "密码长度小于6个字符"});
      return
    }
    if (password != rePassword) {
      callback({status: -1, message: "两次输入的密码不一致"});
      return
    }
    var data = "data:{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"nickname\"" + nickname + "\"}";
    ajax.Post("/api/users", data, (r) => {
      callback(r);
    }, (error) => {
      console.error(error);
    });
  }

  logout() {
    ajax.Delete("/api/sessions", (r) => {
      this.setState({login: false, nickname: "", tags: []});
    }, (error) => {
      console.error(error);
    });
  }

  //
  // updateTags(callback) {
  //   var param = ""
  //   var doms = document.getElementsByName("Tags");
  //   for (var i = 0; i < doms.length; i++) {
  //     if (doms[i].checked) {
  //       param += "&tags=" + doms[i].value;
  //     }
  //   }
  //   ajax.Get("/api/updateTags?" + param.slice(1), (r) => {
  //     callback(r);
  //     this.setState({tags: r.data});
  //   }, (error) => {
  //     console.error(error);
  //   });
  // }

  loadNews(tag) {
    var param = "type=" + this.tagIndexToString[tag] +
        "&page=" + this.state.news[this.tagIndex[tag]].page;
    ajax.Get("/api/news?" + param, (r) => {
      console.log(r);
      if (r.end == false) {
        console.log("bottomTime update");
        this.state.news.push.apply(this.state.news[this.tagIndex[tag]].content, r.news);
        this.state.news[this.tagIndex[tag]].page = this.state.news[this.tagIndex[tag]].page + 1;
      }
      this.setState({news: this.state.news, tag: tag});
      console.log(this.state.news)
    }, (error) => {
      console.error(error);
    });
  }

  formatMsgTime(timespan) {
    var dateTime = new Date(timespan);

    var year = dateTime.getFullYear();
    var month = dateTime.getMonth() + 1;
    var day = dateTime.getDate();
    var hour = dateTime.getHours();
    var minute = dateTime.getMinutes();
    var second = dateTime.getSeconds();
    var now = new Date();
    var now_new = Date.parse(now.toDateString());  //typescript转换写法

    var milliseconds = 0;
    var timeSpanStr;

    milliseconds = now_new - timespan;

    if (milliseconds <= 1000 * 60 * 1) {
      timeSpanStr = '刚刚';
    }
    else if (1000 * 60 * 1 < milliseconds && milliseconds <= 1000 * 60 * 60) {
      timeSpanStr = Math.round((milliseconds / (1000 * 60))) + '分钟前';
    }
    else if (1000 * 60 * 60 * 1 < milliseconds && milliseconds <= 1000 * 60 * 60 * 24) {
      timeSpanStr = Math.round(milliseconds / (1000 * 60 * 60)) + '小时前';
    }
    else if (1000 * 60 * 60 * 24 < milliseconds && milliseconds <= 1000 * 60 * 60 * 24 * 15) {
      timeSpanStr = Math.round(milliseconds / (1000 * 60 * 60 * 24)) + '天前';
    }
    else if (milliseconds > 1000 * 60 * 60 * 24 * 15 && year == now.getFullYear()) {
      timeSpanStr = month + '-' + day + ' ' + hour + ':' + minute;
    } else {
      timeSpanStr = year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
    }
    return timeSpanStr;
  }

  render() {
    return (
        <div>
          <div className="Header">
            <div className="topbar">
              <div className="left clearfix">
                <a id='chooseTag' className='chooseTag bold' onClick={() => {
                  this.setState({taglayer: !this.state.taglayer})
                }}>选择分类</a>
                <div id='taglayer' className='taglayer'
                     style={{"display": this.state.taglayer ? "block" : "none"}}>
                  {this.tags.map((tag, index) => {
                    return (
                        <li onClick={() => {
                          this.loadNews(tag, true);
                        }} key={index}>
                          <a className={tag == this.state.tag ? "wchannel-item active" : "wchannel-item"}>
                            <span>{tag}</span>
                          </a>
                        </li>
                    )
                  })}
                </div>
              </div>
              <div className="right">
                <ul className="item clearfix">
                  {this.state.login ?
                      <li className='userbox'>
                        <a id='userhead' className='userhead bold' onClick={() => {
                          this.setState({userlayer: !this.state.userlayer})
                        }}>{this.state.nickname}</a>
                        <div id='userlayer' className='userlayer'
                             style={{"display": this.state.userlayer ? "block" : "none"}}>
                          <ul>
                            <li>
                              <a className='layeritem' onClick={() => {
                                this.logout()
                              }}>退出</a>
                            </li>
                          </ul>
                        </div>
                      </li>
                      :
                      <li>
                        <a id='triggerRegister'>注册</a>
                        <Dialog triggerID='triggerRegister' title='注册'
                                func={this.register.bind(this)}>
                          <input id='email' className='input' placeholder='请输入邮箱'/>
                          <input id='nickname' className='input' placeholder='昵称长度要求大于6个字符'/>
                          <input id='password' className='input' type='password'
                                 placeholder='密码长度要求大于6个字符'/>
                          <input id='rePassword' className='input' type='password'
                                 placeholder='再次输入密码'/>
                        </Dialog>
                      </li>
                  }
                  {this.state.login ? <li/> :
                      <li className="bold">
                        <a id='triggerLogin'>登录</a>
                        <Dialog triggerID='triggerLogin' title='登录' func={this.login.bind(this)}>
                          <p className='label'>账号</p>
                          <input id='email' className='input' placeholder='请输入邮箱'/>
                          <p className='label'>密码</p>
                          <input id='password' className='input' type='password'
                                 placeholder='请输入密码'/>
                        </Dialog>
                      </li>
                  }
                </ul>
              </div>
            </div>
          </div>


          <div className="container">

            <div className="left content">
              <div className="bui-box slide"></div>

              <div className="feed-infinite-wrapper">
                <ul>

                  {this.state.news[this.tagIndex[this.state.tag]].content.map((news, index) => {
                    var time = this.formatMsgTime(news.time * 1000)
                    return (

                        <li key={index}>
                          <div className="bui-box single-mode">
                            <div className="bui-left single-mode-lbox">
                              <a href={news.docurl} target="_blank" className="img-wrap">
                                <img className="lazy-load-img" src={news.image}/>
                              </a>
                            </div>
                            <div className="single-mode-rbox">
                              <div className="single-mode-rbox-inner">
                                <div className="title-box">
                                  <a href={news.url} target="_blank"
                                     className="link">{news.title}</a>
                                </div>
                                <div className="bui-box footer-bar">
                                  <div className="bui-left footer-bar-left">
                                    <a href="search/?keyword=%E6%97%B6%E6%94%BF"
                                       target="_blank"
                                       className="footer-bar-action tag tag-style-other">{news.type}</a>
                                    {/*<a className="footer-bar-action source">{news.source}</a>*/}
                                    {/*<span className="footer-bar-action">{time}</span>*/}
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </li>
                    )
                  })}
                </ul>
              </div>
            </div>
          </div>
        </div>
    )
  }
}

export
default
App
