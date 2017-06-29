import React from 'react'
import { Link } from 'react-router'
import { hashHistory } from 'react-router'
import { connect } from 'react-redux'
import { browserHistory } from 'react-router'
import crypto from 'crypto'

import './Body.scss'
import './Header.scss'
import Dialog from '../components/Dialog.jsx'
import CheckBox from '../components/CheckBox.jsx'
import ajax from '../utils/ajax.js'

class App extends React.Component {
  constructor(props) {
    super(props);
    this.tagIndex = {
      "推荐": 0,
      "国内": 1,
      "国际": 2,
      "军事": 3,
      "财经": 4,
      "科技": 5,
      "体育": 6,
    }
    var news = new Array(7);
    var time = Date.parse(new Date())/1000;
    for (var i=0;i<news.length;i++) {
      news[i] = {
        bottomTime: time,
        topTime: time,
        content: [],
      }
    }
    this.tags = ["推荐", "国内", "国际", "军事", "财经", "科技", "体育"];
    this.state = {
      news: news,
      tag: "推荐",
      login: false,
      nickname: "",
      tags: [],
      userlayer: false,
      taglayer: false,
    }
  }

  componentDidMount() {
    ajax.Get("/api/checkLogin", (r) => {
      if (r.status==0) {
        this.setState({login: true, nickname: r.data.nickname, tags: r.data.tags, userlayer: false});
      }
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
    var nickname = document.getElementById("nickname").value;
    var password = document.getElementById("password").value;
    if (nickname == "") {
      callback({status: -1, message: "请输入昵称"});
      return
    }
    if (password == "") {
      callback({status: -1, message: "请输入密码"});
      return
    }
    password = crypto.createHash('md5').update(password).digest('hex');
    ajax.Get("/api/login?nickname="+nickname+"&password="+password, (r) => {
      if (r.status==0) {
        console.log(r)
        this.setState({login: true, nickname: r.data.nickname, tags: r.data.tags, userlayer: false});
      } else {
        callback(r);
      }
    }, (error) => {
      console.error(error);
    });
  }

  register(callback) {
    var mail = document.getElementById("mail").value;
    var nickname = document.getElementById("nickname").value;
    var password = document.getElementById("password").value;
    var rePassword = document.getElementById("rePassword").value;

    var regex = /^([0-9A-Za-z\-_\.]+)@([0-9a-z]+\.[a-z]{2,3}(\.[a-z]{2})?)$/g;
    if (!regex.test(mail)) {
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
    password = crypto.createHash('md5').update(password).digest('hex');
    console.log(password);
    ajax.Get("/api/register?mail="+mail+"&nickname="+nickname+"&password="+password, (r) => {
      callback(r);
    }, (error) => {
      console.error(error);
    });
  }
  logout() {
    ajax.Get("/api/logout", (r) => {
      if (r.status==0) {
        this.setState({login: false, nickname: "", tags: []});
      }
    }, (error) => {
      console.error(error);
    });
  }
  updateTags(callback) {
    var param = ""
    var doms = document.getElementsByName("Tags");
    for (var i=0;i < doms.length;i++) {
      if (doms[i].checked){
        param += "&tags="+doms[i].value;
      }
    }
    ajax.Get("/api/updateTags?"+param.slice(1), (r) => {
      callback(r);
      this.setState({tags: r.data});
    }, (error) => {
      console.error(error);
    });
  }



  loadNews(tag, top) {
    var param;
    // 当目前没有新闻的时候，加载历史新闻
    if (this.state.news[this.tagIndex[tag]].content.length==0) {
      top = false;
    }
    if (top) {
      var topTime = this.state.news[this.tagIndex[tag]].topTime;
      var param = "tag="+(tag=="推荐"?"":tag) + "&topTime="+topTime;
    } else {
      var bottomTime = this.state.news[this.tagIndex[tag]].bottomTime;
      var param = "tag="+(tag=="推荐"?"":tag) + "&bottomTime="+bottomTime;
    }
    ajax.Get("/api/feed?"+param, (r) => {
      console.log(r);
      if (r.status==0) {
        if (top) {
          console.log("topTime update");
          this.state.news.unshift.apply(this.state.news[this.tagIndex[tag]].content, r.data.content);
          this.state.news[this.tagIndex[tag]].topTime = r.data.newTime;
        } else {
          console.log("bottomTime update");
          this.state.news.push.apply(this.state.news[this.tagIndex[tag]].content, r.data.content);
          this.state.news[this.tagIndex[tag]].bottomTime = r.data.newTime;
        }
        console.log(tag, this.state.news[this.tagIndex[tag]].topTime, this.state.news[this.tagIndex[tag]].bottomTime);
      }
      this.setState({news: this.state.news, tag: tag});
    }, (error) => {
      console.error(error);
    });
  }

  formatMsgTime (timespan) {
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
          <a id='chooseTag' className='chooseTag bold' onClick={()=>{
            this.setState({taglayer: !this.state.taglayer})
          }}>选择分类</a>
          <div id='taglayer' className='taglayer' style={{"display": this.state.taglayer?"block":"none"}}>
            {this.tags.map((tag, index)=>{
              return (
                <li onClick={()=>{this.loadNews(tag, true);}} key={index}> 
                  <a className={tag==this.state.tag?"wchannel-item active":"wchannel-item"}> 
                    <span>{tag}</span>
                  </a> 
                </li>
              )
            })}
          </div>
        </div>
        <div className="right">
          <ul className="item clearfix">
            {this.state.login?
            <li className='userbox'>
              <a id='userhead' className='userhead bold' onClick={()=>{
                this.setState({userlayer: !this.state.userlayer})
              }}>{this.state.nickname}</a>
              <div id='userlayer' className='userlayer' style={{"display": this.state.userlayer?"block":"none"}}>
                <ul>
                  <li>
                    <a id='triggerTags' className='layeritem'>我的关注</a>
                    <Dialog triggerID='triggerTags' title='我的关注' func={this.updateTags.bind(this)}>
                      {/*<CheckBox options={this.state.checkboxOption} onChange={this.handleChangeCheckBox.bind(this)}/>*/}
                      <label><input name="Tags" type="checkbox" value="国际" defaultChecked={this.state.tags.indexOf("国际")>-1?true:false} />国际 </label> 
                      <label><input name="Tags" type="checkbox" value="国内" defaultChecked={this.state.tags.indexOf("国内")>-1?true:false} />国内 </label> 
                      <label><input name="Tags" type="checkbox" value="军事" defaultChecked={this.state.tags.indexOf("军事")>-1?true:false} />军事 </label> 
                      <label><input name="Tags" type="checkbox" value="财经" defaultChecked={this.state.tags.indexOf("财经")>-1?true:false} />财经 </label> 
                      <label><input name="Tags" type="checkbox" value="科技" defaultChecked={this.state.tags.indexOf("科技")>-1?true:false} />科技 </label> 
                      <label><input name="Tags" type="checkbox" value="体育" defaultChecked={this.state.tags.indexOf("体育")>-1?true:false} />体育 </label> 
                      <label><input name="Tags" type="checkbox" value="娱乐" defaultChecked={this.state.tags.indexOf("娱乐")>-1?true:false} />娱乐 </label> 
                    </Dialog>
                  </li>
                  <li>
                    <a className='layeritem' onClick={()=>{this.logout()}}>退出</a>
                  </li>
                </ul>
              </div>
            </li>
            :
            <li>
              <a id='triggerRegister'>注册</a>
              <Dialog triggerID='triggerRegister' title='注册' func={this.register.bind(this)}>
                <input id='mail' className='input' placeholder='请输入邮箱'/>
                <input id='nickname' className='input' placeholder='昵称长度要求大于6个字符'/>
                <input id='password' className='input' type='password' placeholder='密码长度要求大于6个字符'/>
                <input id='rePassword' className='input' type='password' placeholder='再次输入密码'/>
              </Dialog>
            </li>
            }
            {this.state.login?<li/>:
            <li className="bold">
              <a id='triggerLogin'>登录</a>
              <Dialog triggerID='triggerLogin' title='登录' func={this.login.bind(this)}>
                <p className='label'>账号</p>
                <input id='nickname' className='input' placeholder='请输入昵称'/>
                <p className='label'>密码</p>
                <input id='password' className='input' type='password' placeholder='请输入密码'/>
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

          {this.state.news[this.tagIndex[this.state.tag]].content.map((news, index)=>{
            var time = this.formatMsgTime(news.time*1000)
            return (
              
          <li key={index}>
            <div className="bui-box single-mode">
              <div className="bui-left single-mode-lbox">
                <a href={news.docurl} target="_blank" className="img-wrap">
                  <img className="lazy-load-img" src={news.imgurl}/>
                </a>
              </div> 
              <div className="single-mode-rbox">
                <div className="single-mode-rbox-inner">
                  <div className="title-box">
                    <a href={news.docurl} target="_blank" className="link">{news.title}</a>
                  </div>
                  <div className="bui-box footer-bar">
                    <div className="bui-left footer-bar-left">
                      <a href="search/?keyword=%E6%97%B6%E6%94%BF" target="_blank" className="footer-bar-action tag tag-style-other">{news.tag}</a> 
                      <a className="footer-bar-action source">{news.source}</a> 
                      <span className="footer-bar-action">{time}</span>
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

export default App